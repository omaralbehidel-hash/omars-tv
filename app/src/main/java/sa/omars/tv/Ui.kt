package sa.omars.tv

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.random.Random

/** وحدة القياس: rem = ارتفاع الشاشة ÷ 45 (نفس منطق نسخة الويب)، فيبدو التصميم متطابقًا على أي كثافة. */
object U {
    var rem = 24f
    fun init(ctx: Context) {
        val dm = ctx.resources.displayMetrics
        rem = minOf(dm.widthPixels / 80f, dm.heightPixels / 45f)
    }

    fun r(x: Float): Int = (x * rem + 0.5f).toInt()
    fun r(x: Double): Int = r(x.toFloat())
    fun px(x: Float): Float = x * rem
}

object Pal {
    val BG = Color.parseColor("#000000")
    val PANEL = Color.parseColor("#1C1C1E")
    val GLASS = Color.parseColor("#262628")
    val LINE = Color.parseColor("#33FFFFFF")
    val INK = Color.parseColor("#F5F5F7")
    val INK2 = Color.parseColor("#A1A1A6")
    val WARN = Color.parseColor("#FF9F0A")
    var GOLD = Color.parseColor("#E3C58F")
    var ONGOLD = Color.parseColor("#1D1608")
    var SOFT = Color.parseColor("#2B2417")

    fun accent(name: String) {
        when (name) {
            "orange" -> { GOLD = Color.parseColor("#FF9F0A"); ONGOLD = Color.BLACK; SOFT = Color.parseColor("#2E2108") }
            "white" -> { GOLD = Color.parseColor("#F5F5F7"); ONGOLD = Color.BLACK; SOFT = Color.parseColor("#2A2A2C") }
            "blue" -> { GOLD = Color.parseColor("#0A84FF"); ONGOLD = Color.WHITE; SOFT = Color.parseColor("#0E2236") }
            else -> { GOLD = Color.parseColor("#E3C58F"); ONGOLD = Color.parseColor("#1D1608"); SOFT = Color.parseColor("#2B2417") }
        }
    }
}

fun shape(fill: Int, radius: Float, stroke: Int = 0, sw: Int = 0): GradientDrawable {
    val g = GradientDrawable()
    g.setColor(fill)
    g.cornerRadius = radius
    if (sw > 0) g.setStroke(sw, stroke)
    return g
}

fun lighten(c: Int, a: Float = 0.14f): Int {
    val r = (Color.red(c) + (255 - Color.red(c)) * a).toInt()
    val g = (Color.green(c) + (255 - Color.green(c)) * a).toInt()
    val b = (Color.blue(c) + (255 - Color.blue(c)) * a).toInt()
    return Color.rgb(r, g, b)
}

/** خلفية بحالات: عادي / محدد (القناة الحالية) / مُركّز (إطار أبيض). */
fun focusBg(fill: Int, radius: Float, selFill: Int = Pal.SOFT, selStroke: Int = Pal.GOLD): StateListDrawable {
    val s = StateListDrawable()
    s.addState(intArrayOf(android.R.attr.state_focused), shape(lighten(fill), radius, Color.WHITE, U.r(0.17f).coerceAtLeast(3)))
    s.addState(intArrayOf(android.R.attr.state_selected), shape(selFill, radius, selStroke, U.r(0.1f).coerceAtLeast(2)))
    s.addState(intArrayOf(), shape(fill, radius, Pal.LINE, 1))
    return s
}

fun goldBg(radius: Float): StateListDrawable {
    val s = StateListDrawable()
    s.addState(intArrayOf(android.R.attr.state_focused), shape(lighten(Pal.GOLD, 0.2f), radius, Color.WHITE, U.r(0.17f).coerceAtLeast(3)))
    s.addState(intArrayOf(), shape(Pal.GOLD, radius))
    return s
}

fun label(ctx: Context, text: String = "", sizeRem: Float = 1.1f, color: Int = Pal.INK, face: String = "sans-serif", bold: Boolean = false): TextView {
    val t = TextView(ctx)
    t.text = text
    t.setTextColor(color)
    t.setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, U.px(sizeRem))
    t.typeface = Typeface.create(face, if (bold) Typeface.BOLD else Typeface.NORMAL)
    t.includeFontPadding = false
    t.setSingleLine(false)
    return t
}

/** زر كبسولة (نص). primary = لون التمييز. */
class Btn(ctx: Context, text: String, primary: Boolean = false, sizeRem: Float = 1.05f) : TextView(ctx) {
    init {
        this.text = text
        gravity = Gravity.CENTER
        isFocusable = true
        isClickable = true
        setTextSize(android.util.TypedValue.COMPLEX_UNIT_PX, U.px(sizeRem))
        typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
        includeFontPadding = false
        setPadding(U.r(1.5f), U.r(0.6f), U.r(1.5f), U.r(0.6f))
        minHeight = U.r(2.9f)
        val rad = U.px(2.2f)
        if (primary) {
            setTextColor(Pal.ONGOLD)
            background = goldBg(rad)
        } else {
            setTextColor(Pal.INK)
            background = focusBg(Pal.GLASS, rad)
        }
    }
}

/** زر أيقونة دائري. */
class IBtn(ctx: Context, res: Int, sizeRem: Float = 3f, primary: Boolean = false) : ImageView(ctx) {
    init {
        setImageResource(res)
        isFocusable = true
        isClickable = true
        scaleType = ScaleType.CENTER_INSIDE
        val p = U.r(sizeRem * 0.26f)
        setPadding(p, p, p, p)
        val rad = U.px(sizeRem)
        if (primary) {
            background = goldBg(rad)
            setColorFilter(Pal.ONGOLD)
        } else {
            background = focusBg(Pal.GLASS, rad, Pal.GOLD, Pal.GOLD)
            setColorFilter(Pal.INK)
        }
        layoutParams = LinearLayout.LayoutParams(U.r(sizeRem), U.r(sizeRem))
    }

    fun tint(c: Int) = setColorFilter(c)
}

fun lp(w: Int, h: Int, weight: Float = 0f) = LinearLayout.LayoutParams(w, h, weight)
fun flp(w: Int, h: Int, g: Int = Gravity.NO_GRAVITY) = FrameLayout.LayoutParams(w, h, g)
const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
const val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT

/** LinearLayout بفواصل عبر الهوامش (الفواصل الأصلية لا تُحسب في الأوزان مع RTL فتنزاح الصفوف). */
class GapBox(ctx: Context, private val horiz: Boolean, private val gapPx: Int) : LinearLayout(ctx) {
    class GP(src: ViewGroup.LayoutParams) : LinearLayout.LayoutParams(src) {
        var applied = 0
        init {
            if (src is ViewGroup.MarginLayoutParams) {
                setMargins(src.leftMargin, src.topMargin, src.rightMargin, src.bottomMargin)
            }
            if (src is LinearLayout.LayoutParams) {
                weight = src.weight
                gravity = src.gravity
            }
        }
    }

    init {
        orientation = if (horiz) HORIZONTAL else VERTICAL
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams?) {
        val base = params ?: generateDefaultLayoutParams()
        val p = base as? GP ?: GP(base)
        val want = if (childCount == 0 || index == 0) 0 else gapPx
        if (horiz) p.marginStart = p.marginStart - p.applied + want
        else p.topMargin = p.topMargin - p.applied + want
        p.applied = want
        super.addView(child, index, p)
    }
}

fun hbox(ctx: Context, gapRem: Float = 0f): LinearLayout {
    val l = GapBox(ctx, true, U.r(gapRem))
    l.gravity = Gravity.CENTER_VERTICAL
    return l
}

fun vbox(ctx: Context, gapRem: Float = 0f): LinearLayout = GapBox(ctx, false, U.r(gapRem))

fun gap(w: Int, h: Int): GradientDrawable {
    val g = GradientDrawable()
    g.setSize(w, h)
    g.setColor(Color.TRANSPARENT)
    return g
}

fun Context.vwidth() = resources.displayMetrics.widthPixels

/** مؤشر الصوت الدائري: أعمدة شعاعية حول الشعار تتحرك مع الطيف الحقيقي. */
class VizView(ctx: Context) : View(ctx) {
    var bands = FloatArray(48)
    var real = false
    private val logo: android.graphics.Bitmap =
        android.graphics.BitmapFactory.decodeResource(ctx.resources, R.drawable.logo_symbol)
    private val pLogo = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val pBar = Paint(Paint.ANTI_ALIAS_FLAG)
    private val pRing = Paint(Paint.ANTI_ALIAS_FLAG)
    private val dst = RectF()
    private val rippleT = LongArray(4)   // وقت بدء كل ارتداد (0 = خامل)
    private var lastBass = 0f
    private var lastSpawn = 0L

    init {
        pBar.strokeCap = Paint.Cap.ROUND
        pRing.style = Paint.Style.STROKE
    }

    override fun onDraw(c: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return
        val cx = w / 2f
        val cy = h / 2f
        val rOut = minOf(w, h) / 2f
        val r0 = rOut * 0.64f
        // حلقة خافتة
        pRing.color = Pal.GOLD
        pRing.alpha = 50
        pRing.strokeWidth = maxOf(2f, rOut * 0.012f)
        c.drawCircle(cx, cy, r0 * 0.93f, pRing)
        // مؤشر الارتداد: حلقات تتسع وتتلاشى عند نبضة الجهير
        val now = android.os.SystemClock.uptimeMillis()
        if (real) {
            var bass = 0f
            for (i in 0 until minOf(6, bands.size)) bass += bands[i]
            bass /= 6f
            if (bass > 0.38f && bass - lastBass > 0.05f && now - lastSpawn > 260) {
                for (i in rippleT.indices) if (rippleT[i] == 0L) { rippleT[i] = now; lastSpawn = now; break }
            }
            lastBass = bass
        }
        var live = false
        for (i in rippleT.indices) {
            val t0 = rippleT[i]
            if (t0 == 0L) continue
            val p = (now - t0) / 1100f
            if (p >= 1f) { rippleT[i] = 0L; continue }
            live = true
            val rr = r0 * 0.93f + (rOut * 1.0f - r0 * 0.93f) * p
            pRing.color = Pal.GOLD
            pRing.alpha = ((1f - p) * (1f - p) * 150).toInt()
            pRing.strokeWidth = maxOf(2f, rOut * 0.014f) * (1f - 0.5f * p)
            c.drawCircle(cx, cy, rr, pRing)
        }
        if (live) postInvalidateOnAnimation()
        pRing.alpha = 50
        // الأعمدة (متناظرة يمينًا ويسارًا)
        val n = bands.size
        val total = n * 2
        pBar.color = Pal.GOLD
        pBar.strokeWidth = (2f * Math.PI.toFloat() * r0 / total) * 0.55f
        for (k in 0 until total) {
            val idx = if (k < n) k else total - 1 - k
            val a = if (real) bands[idx].coerceIn(0f, 1f) else 0f
            val len = rOut * 0.03f + a * (rOut - r0 - rOut * 0.03f)
            val ang = (-90.0 + k * 360.0 / total) * Math.PI / 180.0
            val ca = Math.cos(ang).toFloat()
            val sa = Math.sin(ang).toFloat()
            pBar.alpha = if (real) 90 + (a * 165).toInt() else 55
            c.drawLine(cx + ca * r0, cy + sa * r0, cx + ca * (r0 + len), cy + sa * (r0 + len), pBar)
        }
        // الشعار في الوسط
        val lh = r0 * 1.45f
        val lw = lh * logo.width / logo.height
        dst.set(cx - lw / 2f, cy - lh / 2f, cx + lw / 2f, cy + lh / 2f)
        pLogo.alpha = 255
        c.drawBitmap(logo, null, dst, pLogo)
    }
}

/** خلفية النجوم (اختيارية). */
class StarsView(ctx: Context) : View(ctx) {
    private val xs = FloatArray(90) { Random.nextFloat() }
    private val ys = FloatArray(90) { Random.nextFloat() }
    private val rs = FloatArray(90) { Random.nextFloat() * 1.2f + 0.4f }
    private val ph = FloatArray(90) { Random.nextFloat() * 6f }
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val tick = object : Runnable {
        override fun run() {
            if (visibility == VISIBLE && isAttachedToWindow) {
                invalidate()
                postDelayed(this, 600)
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        post(tick)
    }

    override fun onDraw(c: Canvas) {
        val t = System.currentTimeMillis() / 2500.0
        p.color = Color.WHITE
        for (i in 0 until 90) {
            p.alpha = (255 * (0.12 + 0.3 * (0.5 + 0.5 * Math.sin(t + ph[i])))).toInt()
            c.drawCircle(xs[i] * width, ys[i] * height, rs[i] * U.rem / 24f, p)
        }
    }
}
