package sa.omars.tv

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.SurfaceView
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.audio.AudioSink
import androidx.media3.exoplayer.audio.DefaultAudioSink
import androidx.media3.exoplayer.audio.TeeAudioProcessor
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

interface PlayerEvents {
    fun onPlaying()
    fun onBuffering()
    fun onPaused()
    fun onFailed(title: String, msg: String, ytBlocked: Boolean)
    fun onNotice(msg: String)
    fun onBands(b: FloatArray)
}

/** تحليل طيف الصوت من المخرجات الفعلية للمشغّل (بدون أذونات). */
@UnstableApi
class Spectrum(private val sink: (FloatArray) -> Unit) : TeeAudioProcessor.AudioBufferSink {
    @Volatile
    var enabled = false
    private var channels = 2
    private var enc = C.ENCODING_PCM_16BIT
    private val n = 1024
    private val buf = FloatArray(n)
    private var fill = 0
    private var last = 0L
    private val re = FloatArray(n)
    private val im = FloatArray(n)
    private val win = FloatArray(n) { (0.5 - 0.5 * cos(2 * PI * it / (n - 1))).toFloat() }
    private val bars = 48
    private val ex = Executors.newSingleThreadExecutor()
    private val busy = AtomicBoolean(false)
    private val out = FloatArray(bars)
    private val edges = IntArray(bars + 1) { i -> (2 * (230.0 / 2).pow(i.toDouble() / bars)).toInt().coerceAtLeast(2) }

    override fun flush(sampleRateHz: Int, channelCount: Int, encoding: Int) {
        channels = channelCount
        enc = encoding
        fill = 0
    }

    override fun handleBuffer(buffer: ByteBuffer) {
        if (!enabled || enc != C.ENCODING_PCM_16BIT) return
        val now = System.nanoTime()
        if (now - last < 45_000_000L && fill == 0) return
        val sb = buffer.duplicate().order(ByteOrder.nativeOrder()).asShortBuffer()
        var i = 0
        val total = sb.remaining()
        while (i + channels <= total) {
            buf[fill++] = sb.get(sb.position() + i) / 32768f
            i += channels
            if (fill == n) {
                fill = 0
                last = now
                if (busy.compareAndSet(false, true)) {
                    val snap = buf.copyOf()
                    ex.execute {
                        try { compute(snap) } finally { busy.set(false) }
                    }
                }
                return
            }
        }
    }

    private fun compute(src: FloatArray) {
        for (k in 0 until n) {
            re[k] = src[k] * win[k]
            im[k] = 0f
        }
        fft()
        for (b in 0 until bars) {
            var lo = edges[b]
            var hi = edges[b + 1]
            if (hi <= lo) hi = lo + 1
            if (hi > n / 2) hi = n / 2
            if (lo >= hi) lo = hi - 1
            var m = 0f
            for (k in lo until hi) {
                val v = sqrt(re[k] * re[k] + im[k] * im[k])
                if (v > m) m = v
            }
            val v = min(1f, sqrt(m / 28f))
            out[b] = if (v > out[b]) v else out[b] * 0.82f + v * 0.18f
        }
        sink(out.copyOf())
    }

    private fun fft() {
        var j = 0
        for (i in 1 until n) {
            var bit = n shr 1
            while (j and bit != 0) {
                j = j xor bit
                bit = bit shr 1
            }
            j = j xor bit
            if (i < j) {
                val tr = re[i]; re[i] = re[j]; re[j] = tr
                val ti = im[i]; im[i] = im[j]; im[j] = ti
            }
        }
        var len = 2
        while (len <= n) {
            val ang = (-2 * PI / len).toFloat()
            val wr = cos(ang)
            val wi = sin(ang)
            var i = 0
            while (i < n) {
                var cr = 1f
                var ci = 0f
                for (k in 0 until len / 2) {
                    val ur = re[i + k]
                    val ui = im[i + k]
                    val vr = re[i + k + len / 2] * cr - im[i + k + len / 2] * ci
                    val vi = re[i + k + len / 2] * ci + im[i + k + len / 2] * cr
                    re[i + k] = ur + vr
                    im[i + k] = ui + vi
                    re[i + k + len / 2] = ur - vr
                    im[i + k + len / 2] = ui - vi
                    val nr = cr * wr - ci * wi
                    ci = cr * wi + ci * wr
                    cr = nr
                }
                i += len
            }
            len = len shl 1
        }
    }
}

@UnstableApi
private class SpecRenderers(ctx: Context, private val tee: TeeAudioProcessor) : DefaultRenderersFactory(ctx) {
    override fun buildAudioSink(context: Context, enableFloatOutput: Boolean, enableAudioTrackPlaybackParams: Boolean): AudioSink {
        return DefaultAudioSink.Builder(context)
            .setEnableFloatOutput(false)
            .setEnableAudioTrackPlaybackParams(enableAudioTrackPlaybackParams)
            .setAudioProcessors(arrayOf(tee))
            .build()
    }
}

/** مشغّل موحّد: ExoPlayer للبث المباشر والراديو، و WebView صغير ليوتيوب فقط. */
@UnstableApi
class PlayerHost(
    private val ctx: Context,
    private val surface: SurfaceView,
    private val web: WebView,
    private val ev: PlayerEvents
) {
    private var exo: ExoPlayer? = null
    val spectrum = Spectrum { b -> main.post { ev.onBands(b) } }
    private var token = 0
    private var retries = 0
    private var kind = "" // yt | exo | ""
    private var current: Channel? = null
    private var volume = 80
    private var muted = false
    private var ytReady = false
    private var ytPlaying = false
    private var timeout: Runnable? = null

    val isPlaying: Boolean
        get() = when (kind) {
            "exo" -> exo?.isPlaying == true || exo?.playWhenReady == true && exo?.playbackState == Player.STATE_READY
            "yt" -> ytPlaying
            else -> false
        }
    val active: Boolean get() = kind.isNotEmpty()

    init {
        web.addJavascriptInterface(Bridge(), "Y")
    }

    private fun player(): ExoPlayer {
        exo?.let { return it }
        // مخزن مؤقت أكبر + شبكة أصبر + بلا تغيير سرعة التشغيل = بث بلا تقطيع
        val lc = DefaultLoadControl.Builder()
            .setBufferDurationsMs(30_000, 90_000, 4_000, 8_000)
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()
        val http = DefaultHttpDataSource.Factory()
            .setConnectTimeoutMs(15_000)
            .setReadTimeoutMs(20_000)
            .setAllowCrossProtocolRedirects(true)
        val msf = DefaultMediaSourceFactory(DefaultDataSource.Factory(ctx, http))
        val rf = SpecRenderers(ctx, TeeAudioProcessor(spectrum)).setEnableDecoderFallback(true)
        val p = ExoPlayer.Builder(ctx, rf).setLoadControl(lc).setMediaSourceFactory(msf).build()
        p.setVideoSurfaceView(surface)
        p.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when (state) {
                    Player.STATE_BUFFERING -> if (kind == "exo") ev.onBuffering()
                    Player.STATE_READY -> if (kind == "exo") {
                        retries = 0
                        clearTimeout()
                        if (p.playWhenReady) ev.onPlaying() else ev.onPaused()
                    }
                    else -> {}
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (kind == "exo" && isPlaying) {
                    retries = 0
                    clearTimeout()
                    ev.onPlaying()
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                if (kind != "exo") return
                val tok = token
                if (retries < 3) {
                    val wait = 2000L shl retries
                    retries++
                    ev.onBuffering()
                    main.postDelayed({
                        if (tok == token) current?.let { startExo(directUrl(it), tok) }
                    }, wait)
                } else {
                    ev.onFailed("انقطع البث", "تعذر الاستمرار في التشغيل بعد عدة محاولات. تحقق من الاتصال بالإنترنت أو من الرابط.", false)
                }
            }
        })
        exo = p
        applyVolume()
        return p
    }

    private fun clearTimeout() {
        timeout?.let { main.removeCallbacks(it) }
        timeout = null
    }

    private fun armTimeout(tok: Int, ms: Long, onFire: () -> Unit) {
        clearTimeout()
        val r = Runnable { if (tok == token) onFire() }
        timeout = r
        main.postDelayed(r, ms)
    }

    private fun directUrl(c: Channel) = if (c.t == "yt") c.fb else c.u

    fun play(c: Channel, preferDirect: Boolean) {
        stop()
        token++
        retries = 0
        current = c
        val tok = token
        val useYt = c.t == "yt" && !(preferDirect && c.fb.isNotEmpty())
        if (useYt) {
            if (parseYt(c.u) == null) {
                ev.onFailed("الرابط غير صالح", "رابط YouTube غير مدعوم. استخدم رابط القناة youtube.com/channel/UC… أو رابط فيديو مباشر.", false)
                return
            }
            startYt(c, tok)
        } else {
            val u = directUrl(c)
            if (u.isEmpty()) {
                ev.onFailed(c.n, "لم تُضف رابط البث لهذه القناة بعد.", false)
                return
            }
            startExo(u, tok)
        }
    }

    private fun startExo(url: String, tok: Int) {
        if (tok != token) return
        kind = "exo"
        web.visibility = android.view.View.GONE
        surface.visibility = android.view.View.VISIBLE
        val p = player()
        val live = MediaItem.LiveConfiguration.Builder()
            .setTargetOffsetMs(16_000)
            .setMinOffsetMs(9_000)
            .setMaxOffsetMs(60_000)
            .setMinPlaybackSpeed(1f)
            .setMaxPlaybackSpeed(1f)
            .build()
        p.setMediaItem(MediaItem.Builder().setUri(Uri.parse(url)).setLiveConfiguration(live).build())
        p.prepare()
        p.playWhenReady = true
        applyVolume()
        ev.onBuffering()
        armTimeout(tok, 25000) {
            ev.onFailed("تعذر الاتصال بالبث", "لم يبدأ التشغيل خلال المهلة. تحقق من الرابط ومن الإنترنت.", false)
        }
    }

    private fun parseYt(u: String): Pair<String, String>? { // (kind, id)
        val ch = Regex("youtube\\.com/channel/(UC[\\w-]{20,})").find(u)
        if (ch != null) return Pair("ch", ch.groupValues[1])
        val v = Regex("(?:youtu\\.be/|youtube\\.com/(?:watch\\?(?:.*&)?v=|live/|embed/|shorts/))([\\w-]{6,})").find(u)
        if (v != null) return Pair("v", v.groupValues[1])
        return null
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun startYt(c: Channel, tok: Int) {
        kind = "yt"
        ytReady = false
        ytPlaying = false
        surface.visibility = android.view.View.INVISIBLE
        web.visibility = android.view.View.VISIBLE
        val (k, id) = parseYt(c.u)!!
        val vq = when (Store.s.q) {
            "max" -> "&vq=hd1080"
            "med" -> "&vq=hd720"
            "low" -> "&vq=medium"
            else -> ""
        }
        val base = if (k == "ch") "https://www.youtube.com/embed/live_stream?channel=$id&" else "https://www.youtube.com/embed/$id?"
        val src = base + "autoplay=1&playsinline=1&controls=0&rel=0&modestbranding=1&iv_load_policy=3&disablekb=1&fs=0&hl=ar&enablejsapi=1&origin=https%3A%2F%2Fwww.youtube.com" + vq
        val html = "<!DOCTYPE html><html><head><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">" +
            "<style>html,body{margin:0;height:100%;background:#000;overflow:hidden}iframe{position:absolute;top:0;left:0;width:100%;height:100%;border:0}</style></head><body>" +
            "<iframe id=\"p\" src=\"$src\" allow=\"autoplay; encrypted-media\"></iframe><script>" +
            "var s=document.createElement('script');s.src='https://www.youtube.com/iframe_api';document.head.appendChild(s);var pl;" +
            "function onYouTubeIframeAPIReady(){pl=new YT.Player('p',{events:{onReady:function(){Y.onReady()},onStateChange:function(e){Y.onState(e.data)},onError:function(e){Y.onError(e.data)}}});}" +
            "window.cmd=function(f,a){try{pl[f].apply(pl,a||[])}catch(e){}};</script></body></html>"
        val st = web.settings
        st.javaScriptEnabled = true
        st.domStorageEnabled = true
        st.mediaPlaybackRequiresUserGesture = false
        st.cacheMode = WebSettings.LOAD_DEFAULT
        web.loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "utf-8", null)
        ev.onBuffering()
        armTimeout(tok, if (c.fb.isNotEmpty()) 14000 else 25000) {
            if (!fallbackToDirect(tok)) ev.onFailed("تعذر بدء البث", "لم يبدأ التشغيل خلال المهلة. قد لا تكون القناة مباشرة الآن، أو يمنع المالك تشغيلها خارج يوتيوب.", false)
        }
    }

    private fun fallbackToDirect(tok: Int): Boolean {
        val c = current ?: return false
        if (kind != "yt" || c.fb.isEmpty() || tok != token) return false
        web.loadUrl("about:blank")
        ev.onNotice("تم التحويل تلقائيًا إلى البث الاحتياطي")
        startExo(c.fb, tok)
        return true
    }

    inner class Bridge {
        @JavascriptInterface
        fun onReady() {
            val tok = token
            main.post {
                if (tok != token || kind != "yt") return@post
                ytReady = true
                applyVolume()
            }
        }

        @JavascriptInterface
        fun onState(s: Int) {
            val tok = token
            main.post {
                if (tok != token || kind != "yt") return@post
                when (s) {
                    1 -> {
                        ytPlaying = true
                        clearTimeout()
                        ev.onPlaying()
                        applyVolume()
                        if (Store.s.q != "auto") js("cmd('setPlaybackQuality',['" + (if (Store.s.q == "low") "medium" else if (Store.s.q == "med") "hd720" else "hd1080") + "'])")
                    }
                    2 -> {
                        ytPlaying = false
                        ev.onPaused()
                    }
                    3 -> ev.onBuffering()
                }
            }
        }

        @JavascriptInterface
        fun onError(code: Int) {
            val tok = token
            main.post {
                if (tok != token || kind != "yt") return@post
                if (fallbackToDirect(tok)) return@post
                val blocked = code == 101 || code == 150 || code == 153
                clearTimeout()
                ev.onFailed(
                    if (blocked) "تعذر تشغيل القناة هنا" else "تعذر تشغيل القناة",
                    if (blocked) "صاحب القناة لا يسمح بتشغيل هذا البث خارج يوتيوب." else if (code == 100) "هذا الفيديو غير متاح أو لم يعد مباشرًا." else "الرابط غير صالح للتشغيل.",
                    blocked
                )
            }
        }
    }

    private fun js(code: String) {
        try {
            web.evaluateJavascript(code, null)
        } catch (_: Exception) {
        }
    }

    fun togglePlay() {
        when (kind) {
            "exo" -> exo?.let { it.playWhenReady = !it.playWhenReady; if (it.playWhenReady) ev.onPlaying() else ev.onPaused() }
            "yt" -> if (ytPlaying) js("cmd('pauseVideo')") else js("cmd('playVideo')")
        }
    }

    fun pause() {
        when (kind) {
            "exo" -> exo?.playWhenReady = false
            "yt" -> js("cmd('pauseVideo')")
        }
    }

    fun setVolume(v: Int) {
        volume = v.coerceIn(0, 100)
        applyVolume()
    }

    fun setMuted(m: Boolean) {
        muted = m
        applyVolume()
    }

    private fun applyVolume() {
        exo?.volume = if (muted) 0f else volume / 100f
        if (kind == "yt") {
            js("cmd('setVolume',[$volume])")
            js(if (muted) "cmd('mute')" else "cmd('unMute')")
        }
    }

    fun stop() {
        token++
        clearTimeout()
        exo?.let {
            it.stop()
            it.clearMediaItems()
        }
        if (kind == "yt") web.loadUrl("about:blank")
        kind = ""
        ytPlaying = false
    }

    fun release() {
        stop()
        exo?.release()
        exo = null
    }

    fun onPause() {
        // الصوت يستمر في وضع النوم؛ لا نوقف شيئًا هنا.
    }
}
