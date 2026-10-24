package sa.omars.tv

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.TextUtils
import android.view.Gravity
import android.view.KeyEvent
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.view.WindowManager
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.media3.common.util.UnstableApi

@UnstableApi
class MainActivity : Activity(), PlayerEvents {
    val S get() = Store.s

    // ----- الهيكل -----
    lateinit var root: FrameLayout
    private lateinit var shiftBox: LinearLayout
    private lateinit var stars: StarsView
    private lateinit var sheetHost: FrameLayout
    private lateinit var contentLayer: FrameLayout

    // الشريط العلوي
    private lateinit var clockT: TextView
    private lateinit var secsT: TextView
    private lateinit var apT: TextView
    private lateinit var csBox: LinearLayout
    private lateinit var dateBox: LinearLayout
    private lateinit var dateRule: View
    private lateinit var ddayT: TextView
    private lateinit var ddateT: TextView
    private lateinit var wxPill: LinearLayout
    private lateinit var wxCityT: TextView
    private lateinit var wxInfoT: TextView
    private lateinit var topBar: LinearLayout

    // المرحلة
    private lateinit var stage: LinearLayout
    private lateinit var playerCard: FrameLayout
    private lateinit var surface: SurfaceView
    private lateinit var web: WebView
    private lateinit var audPanel: LinearLayout
    private lateinit var audName: TextView
    private lateinit var audNote: TextView
    private lateinit var viz: VizView
    private lateinit var overlay: LinearLayout
    private lateinit var ovT: TextView
    private lateinit var ovM: TextView
    private lateinit var ovB1: Btn
    private lateinit var ovB2: Btn
    private lateinit var liveT: TextView
    private lateinit var controls: LinearLayout
    private lateinit var bPlay: IBtn
    private lateinit var bMute: IBtn
    private lateinit var vLab: TextView
    private lateinit var bViz: Btn
    private lateinit var side: LinearLayout
    private lateinit var segTv: TextView
    private lateinit var segRadio: TextView
    private lateinit var listHead: TextView
    private lateinit var favBtn: Btn
    private lateinit var listBox: LinearLayout
    private lateinit var listScroll: ScrollView

    // الصلوات
    private lateinit var prayRow: LinearLayout
    private lateinit var nextTitle: TextView
    private lateinit var nextName: TextView
    private lateinit var nextCd: TextView
    private lateinit var nextTime: TextView
    private lateinit var nextCard: LinearLayout
    private val pCards = ArrayList<LinearLayout>()
    private val pTimes = ArrayList<TextView>()

    // النوم
    private lateinit var sleepScr: FrameLayout
    private lateinit var sleepIn: LinearLayout
    private lateinit var sClock: TextView
    private lateinit var sAp: TextView
    private lateinit var sDate: TextView
    private lateinit var sWx: TextView
    private lateinit var sNow: TextView
    private lateinit var sHint: TextView
    private val sPrayT = ArrayList<TextView>()

    private lateinit var toastT: TextView
    private lateinit var alertT: TextView

    lateinit var host: PlayerHost
    private lateinit var sheets: Sheets

    // ----- الحالة -----
    var mode = "tv"
    val idx = hashMapOf("tv" to 0, "radio" to 0)
    private val favOnly = hashMapOf("tv" to false, "radio" to false)
    private var playing = false
    private var buffering = false
    private var muted = false
    private var vol = 80
    private var audOn = false
    var sleeping = false
    var endAt = 0L
    private var full = false
    private var sheetOpen = false
    private var prevFocus: View? = null
    private var autoTries = 0
    private var lastDay = ""
    private var today: IntArray? = null
    private var tomorrowFajr: Int? = null
    private var ptMsg = ""
    private var wx: Weather? = null
    private var lastPrayerIdx = -2
    private var alertUntil = 0L
    private var lastShift = 0L
    private var lastDateKey = ""
    private var tone: ToneGenerator? = null

    private val hotHide = Runnable { setControlsAlpha(false) }
    private val tickR = object : Runnable {
        override fun run() {
            tick()
            main.postDelayed(this, 1000 - (System.currentTimeMillis() % 1000))
        }
    }

    companion object {
        const val PREFS = "omars"
        const val KEY_BOOT = "boot"
        private const val REQ_LOC = 41
        var resumeAfterRecreate = false
    }

    // =====================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Store.init(this)
        Pal.accent(S.acc)
        U.init(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUi()
        vol = S.vol
        buildUi()
        host = PlayerHost(this, surface, web, this)
        host.setVolume(vol)
        sheets = Sheets(this)
        applySettings()
        renderList()
        refreshData()
        val resume = resumeAfterRecreate
        resumeAfterRecreate = false
        restoreLast()
        showStart(resume)
        main.post(tickR)
    }

    private fun restoreLast() {
        val m = Store.getStr("lastMode", "tv")
        if (m == "tv" || m == "radio") {
            mode = m
            for (k in listOf("tv", "radio")) {
                val i = Store.getInt("last_$k", 0)
                if (i in 0 until Store.list(k).size) idx[k] = i
            }
        }
        setMode(mode, true)
    }

    private fun showStart(resume: Boolean) {
        val arr = Store.list(mode)
        val i = idx[mode] ?: 0
        if (arr.isEmpty()) {
            showOverlay("لا توجد قنوات", "أضف قناة من «إدارة القنوات».", "إدارة القنوات", { sheets.openManage() }, "", null)
            return
        }
        if (S.autoplay || resume) {
            autoTries = if (resume) 0 else 4
            play(i, true)
        } else {
            val c = arr[i]
            showOverlay(c.n, "اضغط «موافق» لتشغيل القناة، أو اختر قناة أخرى من القائمة.", "تشغيل", { play(i, false) }, "", null)
        }
        main.postDelayed({ if (currentFocus == null) focusCurrentItem() }, 300)
    }

    private fun hideSystemUi() {
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUi()
    }

    override fun onDestroy() {
        main.removeCallbacksAndMessages(null)
        host.release()
        try {
            web.destroy()
        } catch (_: Exception) {
        }
        tone?.release()
        super.onDestroy()
    }

    // =====================================================================
    //  بناء الواجهة
    // =====================================================================
    private fun buildUi() {
        val rem = U.rem
        root = FrameLayout(this)
        root.layoutDirection = View.LAYOUT_DIRECTION_RTL
        root.setBackgroundColor(Pal.BG)
        setContentView(root)

        contentLayer = FrameLayout(this)
        root.addView(contentLayer, FrameLayout.LayoutParams(MATCH, MATCH))
        stars = StarsView(this)
        contentLayer.addView(stars, FrameLayout.LayoutParams(MATCH, MATCH))

        shiftBox = vbox(this)
        shiftBox.setPadding(U.r(2.4f), U.r(1.5f), U.r(2.4f), U.r(1.6f))
        contentLayer.addView(shiftBox, FrameLayout.LayoutParams(MATCH, MATCH))

        buildTop()
        buildStage()
        buildPrayers()
        buildSleep()

        sheetHost = FrameLayout(this)
        sheetHost.visibility = View.GONE
        sheetHost.setBackgroundColor(Color.parseColor("#B8000000"))
        sheetHost.isClickable = true
        root.addView(sheetHost, FrameLayout.LayoutParams(MATCH, MATCH))

        sleepScr.visibility = View.GONE
        root.addView(sleepScr, FrameLayout.LayoutParams(MATCH, MATCH))

        toastT = label(this, "", 1.05f, Pal.INK)
        toastT.setPadding(U.r(1.6f), U.r(0.6f), U.r(1.6f), U.r(0.6f))
        toastT.background = shape(Color.parseColor("#EB141414"), U.px(2f), Pal.LINE, 1)
        toastT.visibility = View.GONE
        val tl = FrameLayout.LayoutParams(WRAP, WRAP, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        tl.bottomMargin = U.r(2f)
        root.addView(toastT, tl)

        alertT = label(this, "", 1.5f, Pal.ONGOLD, "sans-serif-medium")
        alertT.setPadding(U.r(2.2f), U.r(0.8f), U.r(2.2f), U.r(0.8f))
        alertT.background = shape(Pal.GOLD, U.px(3f))
        alertT.visibility = View.GONE
        val al = FrameLayout.LayoutParams(WRAP, WRAP, Gravity.TOP or Gravity.CENTER_HORIZONTAL)
        al.topMargin = U.r(1.2f)
        root.addView(alertT, al)
    }

    private fun buildTop() {
        topBar = hbox(this, 1.1f)
        shiftBox.addView(topBar, lp(MATCH, WRAP))

        // الساعة
        val clockBox = hbox(this, 0.4f)
        clockBox.layoutDirection = View.LAYOUT_DIRECTION_LTR
        clockT = label(this, "00:00", 4f, Pal.INK, "sans-serif-light")
        csBox = vbox(this)
        secsT = label(this, "00", 1.35f, Pal.INK2, "sans-serif-medium")
        apT = label(this, "", 1.35f, Pal.INK2, "sans-serif-medium")
        csBox.addView(secsT)
        csBox.addView(apT)
        csBox.setPadding(0, U.r(0.5f), 0, 0)
        clockBox.addView(clockT)
        clockBox.addView(csBox)
        topBar.addView(clockBox, lp(WRAP, WRAP))

        dateRule = View(this)
        dateRule.setBackgroundColor(Pal.LINE)
        topBar.addView(dateRule, lp(1, U.r(3.6f)))

        dateBox = vbox(this)
        ddayT = label(this, "", 1.7f, Pal.INK, "sans-serif-medium")
        ddateT = label(this, "", 0.95f, Pal.INK2)
        ddateT.setSingleLine(true)
        dateBox.addView(ddayT)
        dateBox.addView(ddateT)
        topBar.addView(dateBox, lp(WRAP, WRAP))

        wxPill = hbox(this, 0.8f)
        wxPill.setPadding(U.r(1.3f), U.r(0.4f), U.r(1.3f), U.r(0.4f))
        wxPill.background = shape(Pal.GLASS, U.px(3f), Pal.LINE, 1)
        wxCityT = label(this, "", 1.1f, Pal.INK, "sans-serif-medium")
        wxInfoT = label(this, "", 1.0f, Pal.INK2)
        wxInfoT.setSingleLine(true)
        wxPill.addView(wxCityT)
        wxPill.addView(wxInfoT)
        topBar.addView(wxPill, lp(WRAP, WRAP))

        val spacer = View(this)
        topBar.addView(spacer, lp(0, 1, 1f))

        val bSleep = IBtn(this, R.drawable.ic_moon)
        val bManage = IBtn(this, R.drawable.ic_menu)
        val bSet = IBtn(this, R.drawable.ic_gear)
        val bFull = IBtn(this, R.drawable.ic_expand)
        val bQuit = IBtn(this, R.drawable.ic_power)
        bSleep.setOnClickListener { sheets.openSleep() }
        bManage.setOnClickListener { sheets.openManage() }
        bSet.setOnClickListener { sheets.openSettings() }
        bFull.setOnClickListener { setFull(true) }
        bQuit.setOnClickListener { finishAndRemoveTask() }
        val tools = hbox(this, 0.6f)
        for (b in listOf(bSleep, bManage, bSet, bFull, bQuit)) tools.addView(b)
        topBar.addView(tools, lp(WRAP, WRAP))

        val mark = ImageView(this)
        mark.setImageResource(R.drawable.logo_symbol)
        mark.scaleType = ImageView.ScaleType.FIT_CENTER
        val ml = lp(U.r(1.9f), U.r(2.8f))
        ml.marginEnd = U.r(0.4f)
        topBar.addView(mark, ml)
    }

    private fun buildStage() {
        stage = hbox(this, 1f)
        stage.gravity = Gravity.NO_GRAVITY
        val sp = lp(MATCH, 0, 1f)
        sp.topMargin = U.r(1f)
        sp.bottomMargin = U.r(1f)
        shiftBox.addView(stage, sp)

        // ---- بطاقة المشغّل ----
        playerCard = FrameLayout(this)
        playerCard.background = shape(Color.BLACK, U.px(1.5f), Pal.LINE, 1)
        playerCard.clipToOutline = true
        playerCard.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(v: View, o: Outline) {
                o.setRoundRect(0, 0, v.width, v.height, U.px(1.5f))
            }
        }

        surface = SurfaceView(this)
        playerCard.addView(surface, FrameLayout.LayoutParams(MATCH, MATCH))
        web = WebView(this)
        web.setBackgroundColor(Color.BLACK)
        web.isFocusable = false
        web.isFocusableInTouchMode = false
        web.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        web.visibility = View.GONE
        playerCard.addView(web, FrameLayout.LayoutParams(MATCH, MATCH))

        // لوحة الصوت (راديو / مؤشر)
        audPanel = vbox(this, 0.9f)
        audPanel.gravity = Gravity.CENTER
        audPanel.setBackgroundColor(Color.parseColor("#0C0C0D"))
        audPanel.visibility = View.GONE
        val logo = ImageView(this)
        logo.setImageResource(R.drawable.logo_symbol)
        audPanel.addView(logo, lp(U.r(4.6f), U.r(7f)))
        audName = label(this, "", 2f, Pal.INK, "sans-serif-medium")
        audName.gravity = Gravity.CENTER
        audPanel.addView(audName, lp(WRAP, WRAP))
        viz = VizView(this)
        val vl = lp(MATCH, U.r(8f))
        vl.leftMargin = U.r(5f)
        vl.rightMargin = U.r(5f)
        audPanel.addView(viz, vl)
        audNote = label(this, "", 1f, Pal.INK2)
        audNote.gravity = Gravity.CENTER
        audPanel.addView(audNote, lp(WRAP, WRAP))
        playerCard.addView(audPanel, FrameLayout.LayoutParams(MATCH, MATCH))

        // رسالة فوق المشغّل
        overlay = vbox(this, 0.8f)
        overlay.gravity = Gravity.CENTER
        overlay.setBackgroundColor(Color.parseColor("#F0000000"))
        overlay.setPadding(U.r(2f), U.r(2f), U.r(2f), U.r(2f))
        overlay.visibility = View.GONE
        overlay.isClickable = true
        ovT = label(this, "", 1.9f, Pal.INK, "sans-serif-medium")
        ovT.gravity = Gravity.CENTER
        ovM = label(this, "", 1.1f, Pal.INK2)
        ovM.gravity = Gravity.CENTER
        ovM.maxWidth = U.r(36f)
        val row = hbox(this, 0.8f)
        row.gravity = Gravity.CENTER
        ovB1 = Btn(this, "", true)
        ovB2 = Btn(this, "")
        row.addView(ovB1)
        row.addView(ovB2)
        overlay.addView(ovT, lp(WRAP, WRAP))
        overlay.addView(ovM, lp(WRAP, WRAP))
        val rl = lp(WRAP, WRAP)
        rl.topMargin = U.r(0.6f)
        overlay.addView(row, rl)
        playerCard.addView(overlay, FrameLayout.LayoutParams(MATCH, MATCH))

        // شارة المباشر
        liveT = label(this, "", 0.95f, Pal.INK, "sans-serif-medium")
        val sp2 = SpannableString("●  مباشر")
        sp2.setSpan(ForegroundColorSpan(Color.parseColor("#FF453A")), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        liveT.text = sp2
        liveT.setPadding(U.r(0.9f), U.r(0.2f), U.r(0.9f), U.r(0.2f))
        liveT.background = shape(Color.parseColor("#99000000"), U.px(2f))
        liveT.visibility = View.GONE
        val ll = FrameLayout.LayoutParams(WRAP, WRAP, Gravity.TOP or Gravity.LEFT)
        ll.topMargin = U.r(1f)
        ll.leftMargin = U.r(1f)
        playerCard.addView(liveT, ll)

        // أزرار التحكم
        controls = hbox(this, 0.7f)
        controls.gravity = Gravity.CENTER
        controls.setPadding(U.r(1f), U.r(2.2f), U.r(1f), U.r(1f))
        val g = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(Color.parseColor("#B3000000"), Color.TRANSPARENT))
        controls.background = g
        val bPrev = IBtn(this, R.drawable.ic_prev)
        bPlay = IBtn(this, R.drawable.ic_play, 3.9f, true)
        val bNext = IBtn(this, R.drawable.ic_next)
        bMute = IBtn(this, R.drawable.ic_vol)
        val bVm = Btn(this, "−")
        vLab = label(this, "", 1.05f, Pal.INK, "sans-serif-medium")
        vLab.gravity = Gravity.CENTER
        vLab.minWidth = U.r(3.4f)
        val bVp = Btn(this, "+")
        bViz = Btn(this, "مؤشر صوتي")
        bPrev.setOnClickListener { step(-1) }
        bPlay.setOnClickListener { togglePlay() }
        bNext.setOnClickListener { step(1) }
        bMute.setOnClickListener { muted = !muted; host.setMuted(muted); refreshUi() }
        bVm.setOnClickListener { setVol(vol - 10) }
        bVp.setOnClickListener { setVol(vol + 10) }
        bViz.setOnClickListener { audOn = !audOn; refreshUi() }
        for (v in listOf<View>(bPrev, bPlay, bNext, bMute, bVm, vLab, bVp, bViz)) controls.addView(v)
        val cl = FrameLayout.LayoutParams(MATCH, WRAP, Gravity.BOTTOM)
        playerCard.addView(controls, cl)
        controls.alpha = 0f
        for (v in listOf<View>(bPrev, bPlay, bNext, bMute, bVm, bVp, bViz)) {
            v.setOnFocusChangeListener { _, f -> if (f) hot() }
        }

        // ---- العمود الجانبي ----
        side = vbox(this, 0.7f)
        val seg = hbox(this, 0.2f)
        seg.setPadding(U.r(0.25f), U.r(0.25f), U.r(0.25f), U.r(0.25f))
        seg.background = shape(Color.parseColor("#12FFFFFF"), U.px(2.4f), Pal.LINE, 1)
        segTv = segBtn("تلفزيون")
        segRadio = segBtn("راديو")
        segTv.setOnClickListener { switchMode("tv") }
        segRadio.setOnClickListener { switchMode("radio") }
        seg.addView(segTv, lp(0, WRAP, 1f))
        seg.addView(segRadio, lp(0, WRAP, 1f))
        side.addView(seg, lp(MATCH, WRAP))

        val head = hbox(this)
        listHead = label(this, "", 0.95f, Pal.INK2)
        favBtn = Btn(this, "★ المفضلة", false, 0.9f)
        favBtn.minHeight = 0
        favBtn.setPadding(U.r(0.8f), U.r(0.15f), U.r(0.8f), U.r(0.15f))
        favBtn.setOnClickListener {
            favOnly[mode] = !(favOnly[mode] ?: false)
            renderList()
        }
        head.addView(listHead, lp(0, WRAP, 1f))
        head.addView(favBtn)
        head.setPadding(U.r(0.4f), 0, U.r(0.4f), 0)
        side.addView(head, lp(MATCH, WRAP))

        listScroll = ScrollView(this)
        listScroll.isVerticalScrollBarEnabled = false
        listScroll.isFocusable = false
        listBox = vbox(this, 0.6f)
        listBox.setPadding(U.r(0.5f), U.r(0.4f), U.r(0.5f), U.r(0.6f))
        listScroll.addView(listBox, FrameLayout.LayoutParams(MATCH, WRAP))
        side.addView(listScroll, lp(MATCH, 0, 1f))

        placeStage()
    }

    private fun segBtn(t: String): TextView {
        val b = Btn(this, t)
        b.background = focusBg(Color.TRANSPARENT, U.px(2.2f), Pal.GOLD, Pal.GOLD)
        b.setPadding(U.r(1f), U.r(0.45f), U.r(1f), U.r(0.45f))
        b.minHeight = 0
        return b
    }

    private fun placeStage() {
        stage.removeAllViews()
        val sideW = lp(U.r(S.listw.toFloat()), MATCH)
        val pl = lp(0, MATCH, 1f)
        if (S.listLeft) {
            stage.addView(playerCard, pl)
            stage.addView(side, sideW)
        } else {
            stage.addView(side, sideW)
            stage.addView(playerCard, pl)
        }
        side.visibility = if (S.list) View.VISIBLE else View.GONE
    }

    private fun buildPrayers() {
        prayRow = hbox(this, 0.8f)
        prayRow.gravity = Gravity.NO_GRAVITY
        shiftBox.addView(prayRow, lp(MATCH, U.r(5.6f)))

        nextCard = vbox(this)
        nextCard.setPadding(U.r(1.2f), U.r(0.6f), U.r(1.2f), U.r(0.6f))
        nextCard.background = shape(Pal.SOFT, U.px(1.1f), Pal.GOLD, 2)
        nextCard.gravity = Gravity.CENTER_VERTICAL
        nextTitle = label(this, "الصلاة القادمة", 1f, Pal.INK, "sans-serif-medium")
        nextName = label(this, "", 0.9f, Pal.INK2)
        nextCd = label(this, "", 2.2f, Pal.GOLD, "sans-serif-medium")
        nextCd.layoutDirection = View.LAYOUT_DIRECTION_LTR
        nextCd.gravity = Gravity.END
        nextTime = label(this, "", 0.8f, Pal.INK2)
        nextCard.addView(nextTitle)
        nextCard.addView(nextName)
        nextCard.addView(nextCd, lp(MATCH, WRAP))
        nextCard.addView(nextTime)
        prayRow.addView(nextCard, lp(0, MATCH, 1.5f))

        for (i in 0 until 6) {
            val c = vbox(this)
            c.gravity = Gravity.CENTER
            c.background = shape(Pal.GLASS, U.px(1.1f), Pal.LINE, 1)
            val n = label(this, PRAYER_NAMES[i], 1f, Pal.INK2)
            n.gravity = Gravity.CENTER
            val t = label(this, "—", 1.9f, Pal.INK, "sans-serif-medium")
            t.gravity = Gravity.CENTER
            c.addView(n, lp(WRAP, WRAP))
            c.addView(t, lp(WRAP, WRAP))
            pCards.add(c)
            pTimes.add(t)
            prayRow.addView(c, lp(0, MATCH, 1f))
        }
    }

    private fun buildSleep() {
        sleepScr = FrameLayout(this)
        sleepScr.setBackgroundColor(Color.BLACK)
        sleepIn = vbox(this, 1.2f)
        sleepIn.gravity = Gravity.CENTER_HORIZONTAL
        val crow = hbox(this, 0.6f)
        crow.layoutDirection = View.LAYOUT_DIRECTION_LTR
        crow.gravity = Gravity.BOTTOM
        sClock = label(this, "00:00", 9f, Color.parseColor("#F5F5F7"), "sans-serif-thin")
        sAp = label(this, "", 2f, Color.parseColor("#8E8E93"))
        crow.addView(sClock)
        crow.addView(sAp)
        sDate = label(this, "", 1.6f, Color.parseColor("#D1D1D6"))
        sDate.gravity = Gravity.CENTER
        sWx = label(this, "", 1.3f, Color.parseColor("#8E8E93"))
        sWx.gravity = Gravity.CENTER
        val prow = hbox(this, 2.4f)
        prow.gravity = Gravity.CENTER
        for (i in 0 until 6) {
            val c = vbox(this)
            c.gravity = Gravity.CENTER_HORIZONTAL
            val n = label(this, PRAYER_NAMES[i], 1.3f, Color.parseColor("#8E8E93"))
            val t = label(this, "—", 1.9f, Color.parseColor("#F5F5F7"), "sans-serif-light")
            c.addView(n)
            c.addView(t)
            sPrayT.add(t)
            prow.addView(c)
        }
        sNow = label(this, "", 1.2f, Color.parseColor("#A1A1A6"))
        sNow.gravity = Gravity.CENTER
        sHint = label(this, "◄ ► الصوت   ▲ ▼ السطوع   موافق للخروج", 0.9f, Color.parseColor("#6E6E73"))
        sHint.gravity = Gravity.CENTER
        sleepIn.addView(crow, lp(WRAP, WRAP))
        sleepIn.addView(sDate, lp(WRAP, WRAP))
        sleepIn.addView(sWx, lp(WRAP, WRAP))
        sleepIn.addView(prow, lp(WRAP, WRAP))
        sleepIn.addView(sNow, lp(WRAP, WRAP))
        sleepIn.addView(sHint, lp(WRAP, WRAP))
        sleepScr.addView(sleepIn, FrameLayout.LayoutParams(WRAP, WRAP, Gravity.CENTER))
        sleepIn.alpha = S.dim.toFloat()
    }

    // =====================================================================
    //  الإعدادات والمظهر
    // =====================================================================
    fun applySettings() {
        csBox.visibility = if (S.secs) View.VISIBLE else View.GONE
        dateBox.visibility = if (S.date) View.VISIBLE else View.GONE
        dateRule.visibility = if (S.date) View.VISIBLE else View.GONE
        wxPill.visibility = if (S.wx) View.VISIBLE else View.GONE
        prayRow.visibility = if (S.prayers && !full) View.VISIBLE else View.GONE
        stars.visibility = if (S.fx) View.VISIBLE else View.GONE
        clockT.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, U.px(4f * S.clock.toFloat()))
        secsT.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, U.px(1.35f * S.clock.toFloat()))
        apT.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, U.px(1.35f * S.clock.toFloat()))
        placeStage()
        sleepIn.alpha = S.dim.toFloat()
        lastDay = ""
        lastDateKey = ""
        tickClock()
        renderWx()
        renderPrayers(true)
        refreshUi()
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putBoolean(KEY_BOOT, S.boot).apply()
    }

    /** يُستدعى بعد الحفظ: يعيد جلب المواقيت والطقس. */
    fun refreshData() {
        val c = S.currentCity()
        bg {
            val z = zoneNow(c)
            val d = z.toLocalDate()
            val t = Prayer.fetch(c, d)
            val tm = Prayer.fetch(c, d.plusDays(1))
            ui {
                today = t
                tomorrowFajr = tm?.get(0)
                ptMsg = if (t == null) "تعذر جلب المواقيت. تحقق من الاتصال." else ""
                lastPrayerIdx = -2
                renderPrayers(true)
            }
            val w = Wx.fetch(c)
            ui {
                if (w != null) wx = w
                renderWx()
            }
        }
        main.removeCallbacks(dataRetry)
        main.postDelayed(dataRetry, if (today == null) 60_000L else 20 * 60_000L)
    }

    private val dataRetry = Runnable { refreshData() }

    // =====================================================================
    //  الساعة والتواريخ
    // =====================================================================
    private fun tickClock() {
        val c = S.currentCity()
        val z = zoneNow(c)
        val (t, ap) = timeText(z, S.h24, S.digits)
        if (clockT.text != t) clockT.text = t
        secsT.text = arStr(p2(z.second), S.digits)
        if (apT.text != ap) apT.text = ap
        sClock.text = t
        sAp.text = ap
        val key = z.toLocalDate().toString() + S.hijri + S.digits
        if (key != lastDateKey) {
            lastDateKey = key
            ddayT.text = dayName(z)
            val g = gregorianText(z, S.digits)
            val h = hijriText(z, S.hijri, S.digits)
            ddateT.text = "$g  •  $h"
            sDate.text = dayName(z) + "  •  " + g + "  •  " + h
            val day = z.toLocalDate().toString()
            if (lastDay.isNotEmpty() && day != lastDay) refreshData()
            lastDay = day
        }
    }

    private fun renderWx() {
        val c = S.currentCity()
        wxCityT.text = c.n
        val w = wx
        if (w == null) {
            wxInfoT.text = "الطقس غير متاح — تحقق من الاتصال"
            sWx.text = ""
            return
        }
        val temp = if (S.unit == "f") Math.round(w.temp * 9 / 5 + 32).toInt() else Math.round(w.temp).toInt()
        val sym = if (S.unit == "f") "°ف" else "°م"
        val line = arStr("$temp$sym", S.digits) + " · " + Wx.text(w.code) + " · رطوبة " + arNum(w.hum, S.digits) + "٪"
        wxInfoT.text = line
        sWx.text = c.n + " · " + line
    }

    private fun renderPrayers(force: Boolean) {
        val t = today
        val c = S.currentCity()
        val z = zoneNow(c)
        val now = z.hour * 3600 + z.minute * 60 + z.second
        for (i in 0 until 6) {
            val s = if (t != null) fmtTimeStr(t[i], S.h24, S.digits) else "—"
            pTimes[i].text = s
            sPrayT[i].text = s
        }
        if (t == null) {
            nextTitle.text = "مواقيت الصلاة"
            nextName.text = ptMsg.ifEmpty { "جارٍ التحميل…" }
            nextCd.text = ""
            nextTime.text = c.n
            for (card in pCards) card.alpha = 1f
            return
        }
        var ni = -1
        for (i in 0 until 6) if (t[i] > now) { ni = i; break }
        val nextSec: Int
        val name: String
        if (ni == -1) {
            nextSec = (tomorrowFajr ?: t[0]) + 86400
            name = PRAYER_NAMES[0]
        } else {
            nextSec = t[ni]
            name = PRAYER_NAMES[ni]
        }
        val left = (nextSec - now).coerceAtLeast(0)
        nextTitle.text = "الصلاة القادمة"
        nextName.text = name
        nextCd.text = arStr(p2(left / 3600) + ":" + p2((left % 3600) / 60) + ":" + p2(left % 60), S.digits)
        nextTime.text = fmtTimeStr(nextSec % 86400, S.h24, S.digits) + " · " + c.n
        for (i in 0 until 6) {
            val isNext = i == ni || (ni == -1 && i == 0)
            pCards[i].alpha = if (ni == -1 || i >= ni) 1f else 0.5f
            pCards[i].background = if (isNext) shape(Pal.SOFT, U.px(1.1f), Pal.GOLD, 2) else shape(Pal.GLASS, U.px(1.1f), Pal.LINE, 1)
        }
        // تنبيه عند دخول الوقت
        val cur = (0 until 6).lastOrNull { t[it] <= now } ?: -1
        if (lastPrayerIdx == -2) lastPrayerIdx = cur
        else if (cur != lastPrayerIdx) {
            lastPrayerIdx = cur
            if (cur >= 0 && cur != 1 && now - t[cur] < 120) announce(PRAYER_NAMES[cur])
        }
    }

    private fun announce(name: String) {
        if (S.alert) {
            alertT.text = "حان الآن موعد صلاة $name"
            alertT.visibility = View.VISIBLE
            alertUntil = System.currentTimeMillis() + 60_000
        }
        if (S.beep) {
            try {
                if (tone == null) tone = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
                tone?.startTone(ToneGenerator.TONE_PROP_BEEP2, 1500)
            } catch (_: Exception) {
            }
        }
    }

    private fun tick() {
        tickClock()
        renderPrayers(false)
        val nowMs = System.currentTimeMillis()
        if (alertT.visibility == View.VISIBLE && nowMs > alertUntil) alertT.visibility = View.GONE
        if (endAt > 0 && nowMs >= endAt) {
            endAt = 0
            stopAll()
            toast("انتهى مؤقت النوم")
            sheets.onTimerChanged()
        }
        if (nowMs - lastShift > 60_000) {
            lastShift = nowMs
            val on = if (sleeping) S.sleepFx else S.burn
            val tx = if (on) (Math.random() * U.px(0.8f) - U.px(0.4f)).toFloat() else 0f
            val ty = if (on) (Math.random() * U.px(0.5f) - U.px(0.25f)).toFloat() else 0f
            shiftBox.translationX = tx
            shiftBox.translationY = ty
            sleepIn.translationX = tx * 6
            sleepIn.translationY = ty * 6
        }
        if (sleeping) updateSleepNow()
        sheets.tick()
    }

    fun timerLabel(): String {
        if (endAt <= 0) return "لا يوجد مؤقت"
        val left = ((endAt - System.currentTimeMillis()) / 1000).toInt().coerceAtLeast(0)
        val h = left / 3600
        val m = (left % 3600) / 60
        val s = left % 60
        return "يتبقى " + arStr((if (h > 0) "$h:" else "") + p2(m) + ":" + p2(s), S.digits)
    }

    fun startTimer(min: Int) {
        endAt = System.currentTimeMillis() + min * 60_000L
        toast("بدأ المؤقت: " + arNum(min, S.digits) + " دقيقة")
    }

    // =====================================================================
    //  القائمة والتشغيل
    // =====================================================================
    private fun logoText(c: Channel): String {
        val parts = c.n.trim().split(" ", "(")
        var w = parts.firstOrNull { it.isNotBlank() } ?: c.n
        if ((w == "إذاعة" || w == "قناة") && parts.size > 1) w = parts[1]
        return if (w.length > 8) w.substring(0, 8) else w
    }

    fun renderList() {
        val arr = Store.list(mode)
        val fo = favOnly[mode] ?: false
        listBox.removeAllViews()
        var shown = 0
        for ((i, c) in arr.withIndex()) {
            if (fo && !c.f) continue
            shown++
            listBox.addView(itemView(i, c), lp(MATCH, WRAP))
        }
        if (shown == 0) {
            val e = label(this, if (fo) "لا توجد قنوات مفضلة" else "لا توجد عناصر", 1f, Pal.INK2)
            e.gravity = Gravity.CENTER
            e.setPadding(0, U.r(2f), 0, U.r(2f))
            listBox.addView(e, lp(MATCH, WRAP))
        }
        listHead.text = (if (mode == "radio") "المحطات " else "القنوات ") + arNum(arr.size, S.digits)
        favBtn.setTextColor(if (fo) Pal.GOLD else Pal.INK)
        updateSelection()
    }

    private fun itemView(i: Int, c: Channel): View {
        val row = hbox(this, 0.8f)
        row.tag = i
        row.isFocusable = true
        row.isClickable = true
        row.setPadding(U.r(0.9f), U.r(0.7f), U.r(0.9f), U.r(0.7f))
        row.background = focusBg(Pal.GLASS, U.px(1.1f))
        val logo = label(this, logoText(c), 0.95f, Pal.GOLD, "sans-serif-medium")
        logo.gravity = Gravity.CENTER
        logo.setSingleLine(true)
        logo.ellipsize = TextUtils.TruncateAt.END
        logo.background = shape(Color.parseColor("#12FFFFFF"), U.px(0.8f), Pal.LINE, 1)
        row.addView(logo, lp(U.r(3.6f), U.r(3.1f)))
        val col = vbox(this)
        val nm = label(this, c.n, 1.1f, Pal.INK, "sans-serif-medium")
        nm.setSingleLine(true)
        nm.ellipsize = TextUtils.TruncateAt.END
        val sub = label(this, if (c.u.isEmpty()) "بلا رابط" else (if (c.f) "★ " else "") + (if (c.t == "audio" || mode == "radio") "إذاعة" else "بث مباشر"), 0.85f, if (c.u.isEmpty()) Pal.WARN else Pal.INK2)
        col.addView(nm)
        col.addView(sub)
        row.addView(col, lp(0, WRAP, 1f))
        val no = label(this, arNum(i + 1, S.digits), 0.85f, Pal.INK2)
        no.setPadding(U.r(0.45f), 0, U.r(0.45f), 0)
        no.background = shape(Color.TRANSPARENT, U.px(0.45f), Pal.LINE, 1)
        row.addView(no)
        row.setOnClickListener {
            if (i == idx[mode] && host.active) togglePlay() else play(i, false)
        }
        return row
    }

    private fun updateSelection() {
        val cur = idx[mode] ?: 0
        for (k in 0 until listBox.childCount) {
            val v = listBox.getChildAt(k)
            val t = v.tag
            if (t is Int) v.isSelected = (t == cur)
        }
    }

    fun focusCurrentItem() {
        val cur = idx[mode] ?: 0
        for (k in 0 until listBox.childCount) {
            val v = listBox.getChildAt(k)
            if (v.tag == cur) { v.requestFocus(); return }
        }
        if (listBox.childCount > 0) listBox.getChildAt(0).requestFocus()
    }

    private fun segState() {
        val onC = Pal.ONGOLD
        segTv.isSelected = mode == "tv"
        segRadio.isSelected = mode == "radio"
        segTv.setTextColor(if (mode == "tv") onC else Pal.INK2)
        segRadio.setTextColor(if (mode == "radio") onC else Pal.INK2)
    }

    fun setMode(m: String, silent: Boolean) {
        mode = m
        segState()
        if (!silent) renderList() else renderList()
        refreshUi()
    }

    private fun switchMode(m: String) {
        if (m == mode) return
        stopAll()
        setMode(m, false)
        val arr = Store.list(m)
        val i = idx[m] ?: 0
        if (arr.isNotEmpty()) {
            if (S.autoplay) play(i, false)
            else showOverlay(arr[i].n, "اضغط «موافق» لتشغيل القناة.", "تشغيل", { play(i, false) }, "", null)
        }
    }

    fun play(i: Int, auto: Boolean) {
        val arr = Store.list(mode)
        if (arr.isEmpty()) return
        val k = i.coerceIn(0, arr.size - 1)
        idx[mode] = k
        Store.putStr("lastMode", mode)
        Store.putInt("last_$mode", k)
        val c = arr[k]
        hideOverlay()
        updateSelection()
        if (!auto) autoTries = 0
        playing = true
        buffering = true
        if (c.u.isEmpty() && c.fb.isEmpty()) {
            host.stop()
            showOverlay(c.n, "لم تُضف رابط البث لهذه القناة بعد. أضف الرابط الرسمي مرة واحدة من «إدارة القنوات».", "إدارة القنوات", { sheets.openManage() }, "التالي", { step(1) })
            return
        }
        if (!isOnline()) {
            failure("لا يوجد اتصال بالإنترنت", "تحقق من اتصال التلفزيون بالشبكة ثم أعد المحاولة.")
            return
        }
        host.setVolume(vol)
        host.setMuted(muted)
        host.play(c, S.direct)
        refreshUi()
    }

    private fun isOnline(): Boolean {
        return try {
            val cm = getSystemService(CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
            cm.activeNetwork != null
        } catch (_: Exception) {
            true
        }
    }

    fun step(d: Int) {
        val arr = Store.list(mode)
        if (arr.isEmpty()) return
        var i = idx[mode] ?: 0
        val fo = favOnly[mode] ?: false
        for (n in 0 until arr.size) {
            i = (i + d + arr.size) % arr.size
            if (!fo || arr[i].f) break
        }
        play(i, false)
    }

    private var zapT: Runnable? = null
    private var zapIdx = -1
    private fun zap(d: Int) {
        val arr = Store.list(mode)
        if (arr.isEmpty()) return
        var i = if (zapIdx >= 0) zapIdx else (idx[mode] ?: 0)
        val fo = favOnly[mode] ?: false
        for (n in 0 until arr.size) {
            i = (i + d + arr.size) % arr.size
            if (!fo || arr[i].f) break
        }
        zapIdx = i
        toast(arr[i].n, 1800)
        zapT?.let { main.removeCallbacks(it) }
        val r = Runnable { val j = zapIdx; zapIdx = -1; play(j, false) }
        zapT = r
        main.postDelayed(r, 650)
    }

    fun stopAll() {
        host.stop()
        playing = false
        buffering = false
        refreshUi()
    }

    private fun togglePlay() {
        if (!host.active) {
            play(idx[mode] ?: 0, false)
            return
        }
        host.togglePlay()
    }

    private fun setVol(v: Int) {
        vol = v.coerceIn(0, 100)
        host.setVolume(vol)
        refreshUi()
    }

    private fun failure(title: String, msg: String) {
        if (autoTries > 0) {
            autoTries--
            val arr = Store.list(mode)
            var j = idx[mode] ?: 0
            for (n in 0 until arr.size) {
                j = (j + 1) % arr.size
                if (arr[j].u.isNotEmpty()) break
            }
            if (j != (idx[mode] ?: 0) && arr.isNotEmpty()) {
                toast("تعذر تشغيل " + arr[idx[mode] ?: 0].n + " — جارٍ تجربة " + arr[j].n, 3500)
                play(j, true)
                return
            }
        }
        val i = idx[mode] ?: 0
        playing = false
        buffering = false
        host.stop()
        showOverlay(title, msg, "إعادة المحاولة", { play(i, false) }, "التالي", { step(1) })
        refreshUi()
    }

    // ----- PlayerEvents -----
    override fun onPlaying() {
        playing = true
        buffering = false
        autoTries = 0
        hideOverlay()
        refreshUi()
    }

    override fun onBuffering() {
        buffering = true
        refreshUi()
    }

    override fun onPaused() {
        playing = false
        refreshUi()
    }

    override fun onFailed(title: String, msg: String, ytBlocked: Boolean) {
        failure(title, msg)
    }

    override fun onNotice(msg: String) = toast(msg, 3000)

    override fun onBands(b: FloatArray) {
        if (audPanel.visibility == View.VISIBLE) {
            viz.bands = b
            viz.real = true
            viz.invalidate()
        }
    }

    // =====================================================================
    //  عرض الحالة
    // =====================================================================
    fun refreshUi() {
        val arr = Store.list(mode)
        val c = arr.getOrNull(idx[mode] ?: 0)
        bPlay.setImageResource(if (playing && !buffering) R.drawable.ic_pause else R.drawable.ic_play)
        bMute.setImageResource(if (muted || vol == 0) R.drawable.ic_mute else R.drawable.ic_vol)
        vLab.text = if (muted) "كتم" else arNum(vol, S.digits) + "٪"
        liveT.visibility = if (playing && !buffering && mode == "tv") View.VISIBLE else View.GONE
        val showAud = mode == "radio" || audOn
        audPanel.visibility = if (showAud) View.VISIBLE else View.GONE
        host.spectrum.enabled = showAud && !sleeping
        audName.text = c?.n ?: ""
        bViz.setTextColor(if (audOn) Pal.GOLD else Pal.INK)
        bViz.visibility = if (mode == "radio") View.GONE else View.VISIBLE
        audNote.text = when {
            buffering -> "جارٍ التحميل…"
            !playing && host.active -> "متوقف مؤقتًا"
            !host.active -> ""
            else -> ""
        }
        if (!playing || muted) viz.real = false
        segState()
        updateSleepNow()
    }

    private fun updateSleepNow() {
        val c = Store.list(mode).getOrNull(idx[mode] ?: 0)
        val t = when {
            !host.active || c == null -> ""
            buffering -> "جارٍ التحميل: " + c.n
            playing -> "◄ يعمل الآن: " + c.n
            else -> "متوقف مؤقتًا: " + c.n
        }
        val tl = if (endAt > 0) "   ·   " + timerLabel() else ""
        sNow.text = t + tl
    }

    fun showOverlay(title: String, msg: String, b1: String, f1: (() -> Unit)?, b2: String, f2: (() -> Unit)?) {
        ovT.text = title
        ovM.text = msg
        ovB1.text = b1
        ovB1.visibility = if (b1.isEmpty()) View.GONE else View.VISIBLE
        ovB1.setOnClickListener { f1?.invoke() }
        ovB2.text = b2
        ovB2.visibility = if (b2.isEmpty()) View.GONE else View.VISIBLE
        ovB2.setOnClickListener { f2?.invoke() }
        overlay.visibility = View.VISIBLE
        playing = false
        buffering = false
        if (!sheetOpen && !sleeping) main.postDelayed({
            if (overlay.visibility == View.VISIBLE && !sheetOpen && !sleeping) {
                (if (b1.isNotEmpty()) ovB1 else ovB2).requestFocus()
            }
        }, 60)
        refreshUi()
    }

    private fun hideOverlay() {
        if (overlay.visibility == View.VISIBLE) {
            val hadFocus = overlay.findFocus() != null
            overlay.visibility = View.GONE
            if (hadFocus) focusCurrentItem()
        }
    }

    fun toast(msg: String, ms: Long = 2200) {
        toastT.text = msg
        toastT.visibility = View.VISIBLE
        main.removeCallbacks(toastHide)
        main.postDelayed(toastHide, ms)
    }

    private val toastHide = Runnable { toastT.visibility = View.GONE }

    private fun setControlsAlpha(on: Boolean) {
        controls.animate().cancel()
        controls.alpha = if (on) 1f else 0f
    }

    private fun hot() {
        setControlsAlpha(true)
        main.removeCallbacks(hotHide)
        main.postDelayed(hotHide, 4000)
    }

    fun setFull(on: Boolean) {
        full = on
        topBar.visibility = if (on) View.GONE else View.VISIBLE
        prayRow.visibility = if (on || !S.prayers) View.GONE else View.VISIBLE
        side.visibility = if (on || !S.list) View.GONE else View.VISIBLE
        stage.setPadding(0, 0, 0, 0)
        shiftBox.setPadding(if (on) 0 else U.r(2.4f), if (on) 0 else U.r(1.5f), if (on) 0 else U.r(2.4f), if (on) 0 else U.r(1.6f))
        val p = stage.layoutParams as LinearLayout.LayoutParams
        p.topMargin = if (on) 0 else U.r(1f)
        p.bottomMargin = if (on) 0 else U.r(1f)
        stage.layoutParams = p
        playerCard.background = shape(Color.BLACK, if (on) 0f else U.px(1.5f), if (on) 0 else Pal.LINE, 1)
        playerCard.outlineProvider = if (on) ViewOutlineProvider.BOUNDS else object : ViewOutlineProvider() {
            override fun getOutline(v: View, o: Outline) {
                o.setRoundRect(0, 0, v.width, v.height, U.px(1.5f))
            }
        }
        if (on) {
            toast("▲ ▼ تبديل القناة   ◄ ► الصوت   موافق إيقاف/تشغيل   رجوع للخروج", 3500)
            hot()
        } else focusCurrentItem()
    }

    // =====================================================================
    //  النوم
    // =====================================================================
    fun enterSleep() {
        if (sleeping) return
        closeSheet(false)
        sleeping = true
        sleepScr.visibility = View.VISIBLE
        sleepIn.alpha = S.dim.toFloat()
        host.spectrum.enabled = false
        updateSleepNow()
    }

    fun exitSleep() {
        if (!sleeping) return
        sleeping = false
        sleepScr.visibility = View.GONE
        refreshUi()
        focusCurrentItem()
    }

    // =====================================================================
    //  النوافذ (Sheets)
    // =====================================================================
    fun openSheet(panel: View) {
        if (sheetOpen) closeSheet(true)
        sheetOpen = true
        prevFocus = currentFocus
        sheetHost.removeAllViews()
        sheetHost.addView(panel, FrameLayout.LayoutParams(WRAP, WRAP, Gravity.CENTER))
        sheetHost.visibility = View.VISIBLE
        contentLayer.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        main.postDelayed({ firstFocusIn(panel) }, 40)
    }

    fun firstFocusIn(v: View) {
        val list = ArrayList<View>()
        v.addFocusables(list, View.FOCUS_DOWN)
        list.firstOrNull { it.isFocusable }?.requestFocus()
    }

    fun closeSheet(quiet: Boolean) {
        if (!sheetOpen) return
        sheetOpen = false
        sheetHost.visibility = View.GONE
        sheetHost.removeAllViews()
        contentLayer.descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        sheets.onClosed()
        if (!quiet) {
            val pf = prevFocus
            if (pf != null && pf.isAttachedToWindow && pf.visibility == View.VISIBLE) pf.requestFocus() else focusCurrentItem()
        }
    }

    // =====================================================================
    //  الموقع (GPS)
    // =====================================================================
    fun askGps() {
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) readLocation()
        else requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), REQ_LOC)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_LOC) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) readLocation()
            else toast("لم يُمنح إذن الموقع — اختر المدينة يدويًا")
        }
    }

    private fun readLocation() {
        try {
            val lm = getSystemService(LOCATION_SERVICE) as android.location.LocationManager
            var best: android.location.Location? = null
            for (p in lm.getProviders(true)) {
                val l = lm.getLastKnownLocation(p) ?: continue
                if (best == null || l.accuracy < best.accuracy) best = l
            }
            if (best != null) {
                S.gpsLat = best.latitude
                S.gpsLon = best.longitude
                toast("تم تحديد موقعك")
            } else toast("تعذر تحديد الموقع — اختر المدينة يدويًا")
        } catch (_: Exception) {
            toast("تعذر تحديد الموقع — اختر المدينة يدويًا")
        }
    }

    // =====================================================================
    //  المفاتيح
    // =====================================================================
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        val k = event.keyCode
        val down = event.action == KeyEvent.ACTION_DOWN

        if (sleeping) {
            if (!down) return true
            when (k) {
                KeyEvent.KEYCODE_DPAD_UP -> { S.dim = (S.dim + 0.05).coerceIn(0.05, 1.0); sleepIn.alpha = S.dim.toFloat(); Store.saveSettings() }
                KeyEvent.KEYCODE_DPAD_DOWN -> { S.dim = (S.dim - 0.05).coerceIn(0.05, 1.0); sleepIn.alpha = S.dim.toFloat(); Store.saveSettings() }
                KeyEvent.KEYCODE_DPAD_LEFT -> setVol(vol - 10)
                KeyEvent.KEYCODE_DPAD_RIGHT -> setVol(vol + 10)
                KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER, KeyEvent.KEYCODE_BACK, KeyEvent.KEYCODE_ESCAPE -> exitSleep()
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_PAUSE -> togglePlay()
                else -> {}
            }
            return true
        }

        if (down) {
            when (k) {
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_PAUSE -> { togglePlay(); hot(); return true }
                KeyEvent.KEYCODE_MEDIA_STOP -> { stopAll(); return true }
                KeyEvent.KEYCODE_MEDIA_NEXT, KeyEvent.KEYCODE_CHANNEL_DOWN -> { if (!sheetOpen) zap(1); return true }
                KeyEvent.KEYCODE_MEDIA_PREVIOUS, KeyEvent.KEYCODE_CHANNEL_UP -> { if (!sheetOpen) zap(-1); return true }
                KeyEvent.KEYCODE_MENU -> { if (!sheetOpen) sheets.openSettings(); return true }
            }
            if (full && !sheetOpen) {
                when (k) {
                    KeyEvent.KEYCODE_DPAD_UP -> { zap(-1); return true }
                    KeyEvent.KEYCODE_DPAD_DOWN -> { zap(1); return true }
                    KeyEvent.KEYCODE_DPAD_LEFT -> { setVol(vol - 5); toast("الصوت " + arNum(vol, S.digits) + "٪", 1200); return true }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> { setVol(vol + 5); toast("الصوت " + arNum(vol, S.digits) + "٪", 1200); return true }
                    KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> { if (event.repeatCount == 0) { togglePlay(); hot() }; return true }
                }
            } else if (k == KeyEvent.KEYCODE_DPAD_UP || k == KeyEvent.KEYCODE_DPAD_DOWN || k == KeyEvent.KEYCODE_DPAD_LEFT || k == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (currentFocus == null) {
                    if (sheetOpen) firstFocusIn(sheetHost) else focusCurrentItem()
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        when {
            sleeping -> exitSleep()
            sheetOpen -> sheets.onBack()
            full -> setFull(false)
            else -> {
                if (System.currentTimeMillis() - backAt < 2500) finishAndRemoveTask()
                else {
                    backAt = System.currentTimeMillis()
                    toast("اضغط رجوع مرة أخرى للخروج")
                }
            }
        }
    }

    private var backAt = 0L
}
