package sa.omars.tv

import android.graphics.Color
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.media3.common.util.UnstableApi

private sealed class Row {
    class Group(val title: String) : Row()
    class Toggle(val label: String, val get: (Settings) -> Boolean, val set: (Settings, Boolean) -> Unit) : Row()
    class Num(val label: String, val min: Double, val max: Double, val step: Double, val get: (Settings) -> Double, val set: (Settings, Double) -> Unit, val fmt: (Double) -> String) : Row()
    class Choice(val label: String, val opts: List<Pair<String, String>>, val get: (Settings) -> String, val set: (Settings, String) -> Unit) : Row()
}

@UnstableApi
class Sheets(private val a: MainActivity) {
    private enum class Kind { NONE, SETTINGS, MANAGE, SLEEP }

    private var kind = Kind.NONE
    private var snapshot = ""
    private var accBefore = ""
    private var formOpen = false
    private var timerLabelT: TextView? = null
    private var customMin = 30
    private var customT: TextView? = null

    private fun panel(wRem: Float, hRem: Float, title: String): LinearLayout {
        val p = vbox(a, 0.9f)
        p.setPadding(U.r(2f), U.r(1.6f), U.r(2f), U.r(1.6f))
        p.background = shape(Pal.PANEL, U.px(1.8f), Pal.LINE, 1)
        p.layoutParams = FrameLayout.LayoutParams(U.r(wRem), U.r(hRem), Gravity.CENTER)
        val t = label(a, title, 1.9f, Pal.INK, "sans-serif-medium")
        p.addView(t, lp(WRAP, WRAP))
        return p
    }

    private fun scroller(inner: View): ScrollView {
        val s = ScrollView(a)
        s.isVerticalScrollBarEnabled = false
        s.isFocusable = false
        s.addView(inner, FrameLayout.LayoutParams(MATCH, WRAP))
        return s
    }

    fun onClosed() {
        kind = Kind.NONE
        formOpen = false
        timerLabelT = null
        customT = null
    }

    fun onBack() {
        when (kind) {
            Kind.SETTINGS -> cancelSettings()
            Kind.MANAGE -> if (formOpen) closeForm() else a.closeSheet(false)
            else -> a.closeSheet(false)
        }
    }

    fun tick() {
        timerLabelT?.text = a.timerLabel()
    }

    fun onTimerChanged() {
        timerLabelT?.text = a.timerLabel()
    }

    // =====================================================================
    //  الإعدادات
    // =====================================================================
    private fun cfg(): List<Row> {
        return listOf(
            Row.Group("العرض"),
            Row.Toggle("نظام 24 ساعة", { it.h24 }, { s, v -> s.h24 = v }),
            Row.Toggle("إظهار الثواني", { it.secs }, { s, v -> s.secs = v }),
            Row.Toggle("إظهار التاريخ", { it.date }, { s, v -> s.date = v }),
            Row.Toggle("إظهار الطقس", { it.wx }, { s, v -> s.wx = v }),
            Row.Toggle("إظهار مواقيت الصلاة", { it.prayers }, { s, v -> s.prayers = v }),
            Row.Toggle("إظهار قائمة القنوات", { it.list }, { s, v -> s.list = v }),
            Row.Toggle("قائمة القنوات على اليسار", { it.listLeft }, { s, v -> s.listLeft = v }),
            Row.Toggle("الأرقام العربية (٠١٢)", { it.digits }, { s, v -> s.digits = v }),
            Row.Num("حجم الساعة", 0.7, 1.4, 0.1, { it.clock }, { s, v -> s.clock = v }, { v -> arStr(String.format(java.util.Locale.ENGLISH, "%.1f", v), Store.s.digits) }),
            Row.Num("حجم أوقات الصلاة", 0.7, 1.4, 0.1, { it.pscale }, { s, v -> s.pscale = v }, { v -> arStr(String.format(java.util.Locale.ENGLISH, "%.1f", v), Store.s.digits) }),
            Row.Num("حجم أزرار الصوت والتحكم", 0.6, 1.4, 0.1, { it.vscale }, { s, v -> s.vscale = v }, { v -> arStr(String.format(java.util.Locale.ENGLISH, "%.1f", v), Store.s.digits) }),
            Row.Num("عرض قائمة القنوات", 14.0, 28.0, 2.0, { it.listw.toDouble() }, { s, v -> s.listw = v.toInt() }, { v -> arStr(v.toInt().toString(), Store.s.digits) }),
            Row.Choice("لون التمييز", listOf("gold" to "ذهبي", "orange" to "برتقالي", "white" to "أبيض"), { it.acc }, { s, v -> s.acc = v }),
            Row.Toggle("خلفية النجوم المتحركة", { it.fx }, { s, v -> s.fx = v }),
            Row.Toggle("منع احتراق الشاشة (إزاحة بطيئة)", { it.burn }, { s, v -> s.burn = v }),
            Row.Group("المدينة والمواقيت"),
            Row.Choice("المدينة", CITIES.map { it.id to it.n }, { it.city }, { s, v -> s.city = v; if (v == "gps") a.askGps() }),
            Row.Choice("طريقة حساب الصلاة", METHODS.map { it.first.toString() to it.second }, { it.method.toString() }, { s, v -> s.method = v.toInt() }),
            Row.Choice("مذهب العصر", listOf("0" to "الجمهور", "1" to "الحنفي"), { it.asr.toString() }, { s, v -> s.asr = v.toInt() }),
            Row.Num("إزاحة التاريخ الهجري (أيام)", -2.0, 2.0, 1.0, { it.hijri.toDouble() }, { s, v -> s.hijri = v.toInt() }, { v -> arStr(v.toInt().toString(), Store.s.digits) }),
            Row.Toggle("تنبيه مرئي عند دخول الوقت", { it.alert }, { s, v -> s.alert = v }),
            Row.Toggle("تنبيه صوتي عند دخول الوقت", { it.beep }, { s, v -> s.beep = v }),
            Row.Choice("وحدة الحرارة", listOf("c" to "مئوية", "f" to "فهرنهايت"), { it.unit }, { s, v -> s.unit = v }),
            Row.Group("التشغيل والصوت"),
            Row.Toggle("تفضيل البث الرسمي المباشر بدل يوتيوب (أخف)", { it.direct }, { s, v -> s.direct = v }),
            Row.Choice("جودة الصورة", listOf("med" to "متوسطة 720p (الأخف والأسلس)", "auto" to "تلقائي (يختارها يوتيوب)", "max" to "الأعلى 1080p (أثقل)", "low" to "الأقل (إنترنت ضعيف)"), { it.q }, { s, v -> s.q = v }),
            Row.Toggle("تشغيل آخر قناة تلقائيًا عند الفتح", { it.autoplay }, { s, v -> s.autoplay = v }),
            Row.Num("مستوى الصوت الافتراضي", 0.0, 100.0, 10.0, { it.vol.toDouble() }, { s, v -> s.vol = v.toInt() }, { v -> arStr(v.toInt().toString(), Store.s.digits) + "٪" }),
            Row.Num("سطوع واجهة وضع النوم", 0.05, 1.0, 0.05, { it.dim }, { s, v -> s.dim = v }, { v -> arStr(Math.round(v * 100).toString(), Store.s.digits) + "٪" }),
            Row.Toggle("حركة الإزاحة في وضع النوم", { it.sleepFx }, { s, v -> s.sleepFx = v }),
            Row.Toggle("التشغيل عند بدء الجهاز (حيث يسمح النظام)", { it.boot }, { s, v -> s.boot = v })
        )
    }

    fun openSettings() {
        kind = Kind.SETTINGS
        snapshot = Store.s.toJson()
        accBefore = Store.s.acc
        val p = panel(62f, 41f, "الإعدادات")
        val acts = hbox(a, 0.7f)
        val bSave = Btn(a, "حفظ", true)
        val bCancel = Btn(a, "إلغاء")
        val bReset = Btn(a, "استعادة الافتراضي")
        bSave.setOnClickListener { saveSettings() }
        bCancel.setOnClickListener { cancelSettings() }
        acts.addView(bSave)
        acts.addView(bCancel)
        acts.addView(bReset)
        p.addView(acts, lp(WRAP, WRAP))
        val rows = vbox(a)
        val sc = scroller(rows)
        p.addView(sc, lp(MATCH, 0, 1f))
        fillRows(rows)
        bReset.setOnClickListener {
            Store.s.copyFrom(Settings())
            a.applySettings()
            rows.removeAllViews()
            fillRows(rows)
            a.toast("استُعيدت القيم الافتراضية — اضغط حفظ للتثبيت")
        }
        a.openSheet(p)
    }

    private fun fillRows(rows: LinearLayout) {
        for (r in cfg()) {
            when (r) {
                is Row.Group -> {
                    val g = label(a, r.title, 1.1f, Pal.GOLD, "sans-serif-medium")
                    g.setPadding(U.r(0.2f), U.r(1.0f), 0, U.r(0.2f))
                    rows.addView(g, lp(MATCH, WRAP))
                }
                is Row.Toggle -> {
                    val b = Btn(a, "", false, 1f)
                    val upd = { b.text = if (r.get(Store.s)) "مفعّل" else "متوقف"; b.setTextColor(if (r.get(Store.s)) Pal.GOLD else Pal.INK2) }
                    upd()
                    b.setOnClickListener {
                        r.set(Store.s, !r.get(Store.s))
                        upd()
                        a.applySettings()
                    }
                    rows.addView(rowOf(r.label, b), lp(MATCH, WRAP))
                }
                is Row.Num -> {
                    val box = hbox(a, 0.5f)
                    val m = Btn(a, "−", false, 1.2f)
                    val v = label(a, "", 1.05f, Pal.GOLD, "sans-serif-medium")
                    v.gravity = Gravity.CENTER
                    v.minWidth = U.r(5f)
                    val pl = Btn(a, "+", false, 1.2f)
                    val upd = { v.text = r.fmt(r.get(Store.s)) }
                    upd()
                    val change = { d: Int ->
                        val nv = (r.get(Store.s) + d * r.step).coerceIn(r.min, r.max)
                        r.set(Store.s, Math.round(nv * 100) / 100.0)
                        upd()
                        a.applySettings()
                    }
                    m.setOnClickListener { change(-1) }
                    pl.setOnClickListener { change(1) }
                    box.addView(m)
                    box.addView(v)
                    box.addView(pl)
                    rows.addView(rowOf(r.label, box), lp(MATCH, WRAP))
                }
                is Row.Choice -> {
                    val b = Btn(a, "", false, 1f)
                    val upd = { b.text = (r.opts.firstOrNull { it.first == r.get(Store.s) } ?: r.opts[0]).second }
                    upd()
                    b.setOnClickListener {
                        val i = r.opts.indexOfFirst { it.first == r.get(Store.s) }
                        r.set(Store.s, r.opts[(i + 1) % r.opts.size].first)
                        upd()
                        a.applySettings()
                    }
                    rows.addView(rowOf(r.label, b), lp(MATCH, WRAP))
                }
            }
        }
    }

    private fun rowOf(text: String, ctl: View): LinearLayout {
        val r = hbox(a, 1f)
        r.setPadding(U.r(0.2f), U.r(0.45f), U.r(0.2f), U.r(0.45f))
        val t = label(a, text, 1.1f, Pal.INK)
        r.addView(t, lp(0, WRAP, 1f))
        r.addView(ctl, lp(WRAP, WRAP))
        val line = vbox(a)
        line.addView(r, lp(MATCH, WRAP))
        val ln = View(a)
        ln.setBackgroundColor(Pal.LINE)
        line.addView(ln, lp(MATCH, 1))
        return line
    }

    private fun saveSettings() {
        val s = Store.s
        val before = org.json.JSONObject(snapshot)
        val needData = before.optString("city") != s.city || before.optInt("method") != s.method ||
            before.optInt("asr") != s.asr || before.optString("unit") != s.unit ||
            before.optDouble("gpsLat") != s.gpsLat || before.optDouble("gpsLon") != s.gpsLon
        Store.saveSettings()
        val accChanged = accBefore != s.acc
        a.closeSheet(true)
        a.applySettings()
        if (needData) a.refreshData()
        if (accChanged) {
            MainActivity.resumeAfterRecreate = true
            a.recreate()
            return
        }
        a.toast("تم حفظ الإعدادات")
        a.focusCurrentItem()
    }

    private fun cancelSettings() {
        Store.s.load(snapshot)
        a.closeSheet(true)
        a.applySettings()
        a.focusCurrentItem()
    }

    // =====================================================================
    //  النوم
    // =====================================================================
    fun openSleep() {
        kind = Kind.SLEEP
        val p = panel(46f, 30f, "وضع النوم")
        val hint = label(a, "شاشة سوداء هادئة بساعة خافتة. الصوت يستمر (فيديو أو راديو). ◄ ► الصوت · ▲ ▼ السطوع · موافق للخروج.", 1f, Pal.INK2)
        p.addView(hint, lp(MATCH, WRAP))
        val chips = hbox(a, 0.7f)
        for (m in listOf(15, 30, 60, 120)) {
            val b = Btn(a, arNum(m, Store.s.digits) + " دقيقة")
            b.setOnClickListener { a.startTimer(m); tick() }
            chips.addView(b)
        }
        p.addView(chips, lp(WRAP, WRAP))
        val cust = hbox(a, 0.6f)
        val mm = Btn(a, "−", false, 1.2f)
        val ct = label(a, "", 1.1f, Pal.GOLD, "sans-serif-medium")
        ct.gravity = Gravity.CENTER
        ct.minWidth = U.r(6f)
        val pp = Btn(a, "+", false, 1.2f)
        val go = Btn(a, "مدة مخصصة")
        customT = ct
        val upd = { ct.text = arNum(customMin, Store.s.digits) + " د" }
        upd()
        mm.setOnClickListener { customMin = (customMin - 10).coerceAtLeast(5); upd() }
        pp.setOnClickListener { customMin = (customMin + 10).coerceAtMost(720); upd() }
        go.setOnClickListener { a.startTimer(customMin); tick() }
        cust.addView(mm)
        cust.addView(ct)
        cust.addView(pp)
        cust.addView(go)
        p.addView(cust, lp(WRAP, WRAP))
        val tl = label(a, a.timerLabel(), 1.6f, Pal.GOLD, "sans-serif-light")
        timerLabelT = tl
        p.addView(tl, lp(WRAP, WRAP))
        val acts = hbox(a, 0.7f)
        val now = Btn(a, "دخول وضع النوم", true)
        val cancel = Btn(a, "إلغاء المؤقت")
        val close = Btn(a, "إغلاق")
        now.setOnClickListener { a.enterSleep() }
        cancel.setOnClickListener { a.endAt = 0; a.toast("أُلغي المؤقت"); tick() }
        close.setOnClickListener { a.closeSheet(false) }
        acts.addView(now)
        acts.addView(cancel)
        acts.addView(close)
        p.addView(acts, lp(WRAP, WRAP))
        a.openSheet(p)
    }

    // =====================================================================
    //  إدارة القنوات
    // =====================================================================
    private var mTab = "tv"
    private var mcf = -1
    private lateinit var mHost: LinearLayout
    private lateinit var tabTv: Btn
    private lateinit var tabRadio: Btn
    private lateinit var formBox: LinearLayout
    private lateinit var listScroll: ScrollView
    private lateinit var fName: EditText
    private lateinit var fUrl: EditText
    private lateinit var fFb: EditText
    private lateinit var fType: Btn
    private lateinit var fMsg: TextView
    private var fTypeV = "yt"
    private var editIdx = -1

    fun openManage() {
        kind = Kind.MANAGE
        formOpen = false
        mTab = a.mode
        mcf = -1
        val p = panel(62f, 41f, "إدارة القنوات")
        val tabs = hbox(a, 0.7f)
        tabTv = Btn(a, "القنوات")
        tabRadio = Btn(a, "المحطات")
        tabTv.setOnClickListener { mTab = "tv"; mcf = -1; renderManage() }
        tabRadio.setOnClickListener { mTab = "radio"; mcf = -1; renderManage() }
        tabs.addView(tabTv)
        tabs.addView(tabRadio)
        val add = Btn(a, "إضافة", true)
        val close = Btn(a, "إغلاق")
        add.setOnClickListener { openForm(-1) }
        close.setOnClickListener { a.closeSheet(false) }
        tabs.addView(add)
        tabs.addView(close)
        p.addView(tabs, lp(WRAP, WRAP))
        mHost = vbox(a, 0.45f)
        listScroll = scroller(mHost)
        p.addView(listScroll, lp(MATCH, 0, 1f))
        buildForm()
        p.addView(formBox, lp(MATCH, WRAP))
        formBox.visibility = View.GONE
        renderManage()
        a.openSheet(p)
    }

    private fun renderManage() {
        val arr = Store.list(mTab)
        tabTv.setTextColor(if (mTab == "tv") Pal.GOLD else Pal.INK)
        tabRadio.setTextColor(if (mTab == "radio") Pal.GOLD else Pal.INK)
        mHost.removeAllViews()
        if (arr.isEmpty()) {
            val e = label(a, "لا توجد عناصر", 1f, Pal.INK2)
            e.gravity = Gravity.CENTER
            mHost.addView(e, lp(MATCH, WRAP))
            return
        }
        for ((i, c) in arr.withIndex()) {
            val row = hbox(a, 0.4f)
            val nm = Btn(a, c.n + "   " + (if (c.u.isEmpty()) "بلا رابط" else c.c), false, 1f)
            nm.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            nm.setOnClickListener { openForm(i) }
            val fav = Btn(a, if (c.f) "★" else "☆", false, 1f)
            val up = Btn(a, "▲", false, 1f)
            val dn = Btn(a, "▼", false, 1f)
            val del = Btn(a, if (mcf == i) "تأكيد الحذف؟" else "حذف", false, 1f)
            fav.setOnClickListener { c.f = !c.f; Store.saveLists(); renderManage(); a.renderList(); focusIn(i, 1) }
            up.setOnClickListener { move(i, -1) }
            dn.setOnClickListener { move(i, 1) }
            del.setOnClickListener {
                if (mcf != i) {
                    mcf = i
                    renderManage()
                    focusIn(i, 4)
                } else {
                    mcf = -1
                    delete(i)
                }
            }
            row.addView(nm, lp(0, WRAP, 1f))
            row.addView(fav)
            row.addView(up)
            row.addView(dn)
            row.addView(del)
            mHost.addView(row, lp(MATCH, WRAP))
        }
    }

    private fun focusIn(i: Int, child: Int) {
        val row = mHost.getChildAt(i) as? LinearLayout ?: return
        row.getChildAt(child)?.requestFocus()
    }

    private fun move(i: Int, d: Int) {
        val arr = Store.list(mTab)
        val j = i + d
        if (j < 0 || j >= arr.size) return
        val t = arr[i]
        arr[i] = arr[j]
        arr[j] = t
        if (mTab == a.mode) {
            val cur = a.idx[mTab] ?: 0
            if (cur == i) a.idx[mTab] = j else if (cur == j) a.idx[mTab] = i
        }
        Store.saveLists()
        renderManage()
        a.renderList()
        focusIn(j, if (d < 0) 2 else 3)
    }

    private fun delete(i: Int) {
        val arr = Store.list(mTab)
        val wasCur = mTab == a.mode && i == (a.idx[mTab] ?: 0)
        arr.removeAt(i)
        Store.saveLists()
        if (mTab == a.mode) {
            var cur = a.idx[mTab] ?: 0
            if (i < cur) cur--
            a.idx[mTab] = cur.coerceIn(0, (arr.size - 1).coerceAtLeast(0))
            if (wasCur) a.stopAll()
        }
        renderManage()
        a.renderList()
        a.toast("تم الحذف")
    }

    private fun field(hint: String, ltr: Boolean): EditText {
        val e = EditText(a)
        e.hint = hint
        e.setHintTextColor(Pal.INK2)
        e.setTextColor(Pal.INK)
        e.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, U.px(1.05f))
        e.setSingleLine(true)
        e.imeOptions = EditorInfo.IME_ACTION_DONE
        e.inputType = InputType.TYPE_CLASS_TEXT or (if (ltr) InputType.TYPE_TEXT_VARIATION_URI else 0)
        e.setPadding(U.r(1f), U.r(0.55f), U.r(1f), U.r(0.55f))
        e.background = focusBg(Color.parseColor("#101012"), U.px(0.9f))
        if (ltr) {
            e.textDirection = View.TEXT_DIRECTION_LTR
            e.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        }
        return e
    }

    private fun buildForm() {
        formBox = vbox(a, 0.5f)
        formBox.setPadding(0, U.r(0.6f), 0, 0)
        fName = field("الاسم", false)
        fUrl = field("الرابط (https://…)", true)
        fFb = field("رابط بث احتياطي (اختياري)", true)
        fType = Btn(a, "", false, 1f)
        fMsg = label(a, "", 1f, Pal.WARN)
        fType.setOnClickListener {
            if (mTab == "tv") {
                fTypeV = if (fTypeV == "yt") "hls" else "yt"
                typeLabel()
            }
        }
        val row = hbox(a, 0.7f)
        val save = Btn(a, "حفظ وتجربة", true)
        val cancel = Btn(a, "إلغاء")
        save.setOnClickListener { saveForm() }
        cancel.setOnClickListener { closeForm() }
        row.addView(save)
        row.addView(cancel)
        formBox.addView(fName, lp(MATCH, WRAP))
        formBox.addView(fUrl, lp(MATCH, WRAP))
        formBox.addView(fFb, lp(MATCH, WRAP))
        formBox.addView(fType, lp(WRAP, WRAP))
        formBox.addView(fMsg, lp(MATCH, WRAP))
        formBox.addView(row, lp(WRAP, WRAP))
    }

    private fun typeLabel() {
        fType.text = when {
            mTab == "radio" -> "النوع: بث صوتي"
            fTypeV == "yt" -> "النوع: YouTube"
            else -> "النوع: بث مباشر متوافق"
        }
        fFb.visibility = if (mTab == "tv" && fTypeV == "yt") View.VISIBLE else View.GONE
    }

    private fun openForm(i: Int) {
        editIdx = i
        formOpen = true
        val c = if (i >= 0) Store.list(mTab)[i] else null
        fName.setText(c?.n ?: "")
        fUrl.setText(c?.u ?: "")
        fFb.setText(c?.fb ?: "")
        fTypeV = if (mTab == "radio") "audio" else (c?.t ?: "hls").let { if (it == "yt") "yt" else "hls" }
        if (c == null && mTab == "tv") fTypeV = "hls"
        fMsg.text = ""
        typeLabel()
        listScroll.visibility = View.GONE
        formBox.visibility = View.VISIBLE
        fName.requestFocus()
    }

    private fun closeForm() {
        formOpen = false
        formBox.visibility = View.GONE
        listScroll.visibility = View.VISIBLE
        renderManage()
    }

    private fun saveForm() {
        val n = fName.text.toString().trim()
        val u = fUrl.text.toString().trim()
        val fb = fFb.text.toString().trim()
        if (n.isEmpty()) { fMsg.text = "أدخل اسمًا."; fName.requestFocus(); return }
        if (u.isNotEmpty() && !Regex("^https?://\\S+$").matches(u)) { fMsg.text = "الرابط يجب أن يبدأ بـ https://"; fUrl.requestFocus(); return }
        val yt = u.contains("youtube.com") || u.contains("youtu.be")
        if (mTab == "tv" && fTypeV == "yt" && u.isNotEmpty() && !Regex("youtube\\.com/channel/UC[\\w-]{20,}|youtu\\.be/[\\w-]{6,}|youtube\\.com/(watch\\?(.*&)?v=|live/|embed/|shorts/)[\\w-]{6,}").containsMatchIn(u)) {
            fMsg.text = "رابط YouTube غير مدعوم. استخدم youtube.com/channel/UC… أو رابط فيديو مباشر."
            fUrl.requestFocus()
            return
        }
        if (fTypeV == "yt" && !yt && u.isNotEmpty()) { fMsg.text = "الرابط ليس رابط YouTube. اختر النوع «بث مباشر متوافق»."; return }
        val arr = Store.list(mTab)
        val type = if (mTab == "radio") "audio" else fTypeV
        val ch = if (editIdx >= 0) arr[editIdx] else Channel(n, if (mTab == "radio") "إذاعة" else "قناة", u, type)
        ch.n = n
        ch.u = u
        ch.t = type
        ch.fb = if (type == "yt") fb else ""
        if (editIdx < 0) arr.add(ch)
        Store.saveLists()
        val k = arr.indexOf(ch)
        formOpen = false
        a.closeSheet(true)
        if (mTab != a.mode) a.setMode(mTab, false)
        a.renderList()
        if (u.isNotEmpty()) a.play(k, false) else a.idx[mTab] = k
        a.focusCurrentItem()
    }
}
