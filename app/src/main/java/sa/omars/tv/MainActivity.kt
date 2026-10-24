package sa.omars.tv

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.JavascriptInterface
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.webkit.WebViewAssetLoader

/**
 * OMAR'S TV — يعرض واجهة الويب المرفقة (assets) داخل WebView بملء الشاشة.
 * يتم تحميل الملفات عبر https://appassets.androidplatform.net حتى يكون للصفحة أصل (origin) صحيح
 * يقبله مشغّل YouTube المضمّن، ويعمل التخزين المحلي للإعدادات والقنوات بشكل دائم.
 */
class MainActivity : Activity() {

    private lateinit var web: WebView
    private var pendingGeoOrigin: String? = null
    private var pendingGeoCallback: GeolocationPermissions.Callback? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUi()

        web = WebView(this)
        web.setBackgroundColor(Color.parseColor("#000000"))
        web.isFocusable = true
        web.isFocusableInTouchMode = true
        setContentView(web)

        web.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mediaPlaybackRequiresUserGesture = false
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            setGeolocationEnabled(true)
            allowFileAccess = false
            allowContentAccess = false
            cacheMode = WebSettings.LOAD_DEFAULT
            textZoom = 100
            setSupportZoom(false)
            loadWithOverviewMode = false
            useWideViewPort = false
        }

        val loader = WebViewAssetLoader.Builder()
            .setDomain(HOST)
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(this))
            .build()

        web.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? = loader.shouldInterceptRequest(request.url)

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                // لا نسمح للصفحة الرئيسية بالانتقال إلى مواقع خارجية
                return request.isForMainFrame && request.url.host != HOST
            }

            override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
                recreate()
                return true
            }
        }

        web.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(
                origin: String,
                callback: GeolocationPermissions.Callback
            ) {
                if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    callback.invoke(origin, true, false)
                } else {
                    pendingGeoOrigin = origin
                    pendingGeoCallback = callback
                    requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQ_LOCATION)
                }
            }

            override fun onPermissionRequest(request: PermissionRequest) {
                request.deny() // لا نحتاج كاميرا أو ميكروفون
            }
        }

        web.addJavascriptInterface(Bridge(), "Android")
        web.requestFocus()
        web.loadUrl("https://$HOST/assets/index.html")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_LOCATION) {
            val ok = grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            pendingGeoCallback?.invoke(pendingGeoOrigin, ok, false)
            pendingGeoCallback = null
            pendingGeoOrigin = null
        }
    }

    @Volatile private var typing = false

    /** مفاتيح الريموت: الأسهم والموافق تُمرَّر مباشرة للواجهة (حتى لا يأخذها مشغّل يوتيوب). */
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val nav = when (event.keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> "ArrowUp"
            KeyEvent.KEYCODE_DPAD_DOWN -> "ArrowDown"
            KeyEvent.KEYCODE_DPAD_LEFT -> "ArrowLeft"
            KeyEvent.KEYCODE_DPAD_RIGHT -> "ArrowRight"
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_NUMPAD_ENTER -> "Enter"
            else -> null
        }
        if (nav != null && !typing) {
            if (event.action == KeyEvent.ACTION_DOWN) {
                web.evaluateJavascript("window.omarsDpad && window.omarsDpad('$nav')", null)
            }
            return true
        }
        if (event.action == KeyEvent.ACTION_DOWN) {
            val cmd = when (event.keyCode) {
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_PAUSE -> "playpause"
                KeyEvent.KEYCODE_MEDIA_STOP -> "stop"
                KeyEvent.KEYCODE_MEDIA_NEXT, KeyEvent.KEYCODE_CHANNEL_DOWN -> "next"
                KeyEvent.KEYCODE_MEDIA_PREVIOUS, KeyEvent.KEYCODE_CHANNEL_UP -> "prev"
                else -> null
            }
            if (cmd != null) {
                web.evaluateJavascript("window.omarsKey && window.omarsKey('$cmd')", null)
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        // الواجهة تقرر: إغلاق نافذة / خروج من ملء الشاشة / وضع النوم، أو تطلب ضغطة ثانية للخروج
        web.evaluateJavascript("window.omarsBack ? window.omarsBack() : false") { r ->
            if (r == "false" || r == "null") finish()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        web.onResume()
    }

    override fun onPause() {
        web.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        web.removeJavascriptInterface("Android")
        web.destroy()
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUi()
    }

    @Suppress("DEPRECATION")
    private fun hideSystemUi() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    /** جسر بسيط بين الواجهة والنظام (خيار التشغيل عند بدء الجهاز). */
    inner class Bridge {
        @JavascriptInterface
        fun setBoot(on: Boolean) {
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().putBoolean(KEY_BOOT, on).apply()
        }

        @JavascriptInterface
        fun exitApp() {
            runOnUiThread { finishAndRemoveTask() }
        }

        @JavascriptInterface
        fun setTyping(on: Boolean) {
            typing = on
        }
    }

    companion object {
        const val HOST = "appassets.androidplatform.net"
        const val PREFS = "omars"
        const val KEY_BOOT = "boot"
        private const val REQ_LOCATION = 41
    }
}
