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

fun hbox(ctx: Context, gapRem: Float = 0f): LinearLayout {
    val l = LinearLayout(ctx)
    l.orientation = LinearLayout.HORIZONTAL
    l.gravity = Gravity.CENTER_VERTICAL
    if (gapRem > 0) {
        l.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        l.dividerDrawable = gap(U.r(gapRem), 1)
    }
    return l
}

fun vbox(ctx: Context, gapRem: Float = 0f): LinearLayout {
    val l = LinearLayout(ctx)
    l.orientation = LinearLayout.VERTICAL
    if (gapRem > 0) {
        l.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        l.dividerDrawable = gap(1, U.r(gapRem))
    }
    return l
}

fun gap(w: Int, h: Int): GradientDrawable {
    val g = GradientDrawable()
    g.setSize(w, h)
    g.setColor(Color.TRANSPARENT)
    return g
}

fun Context.vwidth() = resources.displayMetrics.widthPixels

/** مؤشر الصوت: أعمدة من الطيف الحقيقي، وعند غيابه شريط خافت. */
class VizView(ctx: Context) : View(ctx) {
    var bands = FloatArray(48)
    var real = false
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rect = RectF()

    override fun onDraw(c: Canvas) {
        val n = bands.size
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f) return
        val bw = w / n
        p.color = Pal.GOLD
        p.alpha = if (real) 230 else 60
        for (i in 0 until n) {
            val a = if (real) bands[i] else 0f
            val bh = maxOf(U.px(0.18f), a * h)
            val x = i * bw + bw * 0.18f
            rect.set(x, (h - bh) / 2f, x + bw * 0.64f, (h + bh) / 2f)
            c.drawRoundRect(rect, bw * 0.32f, bw * 0.32f, p)
        }
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
