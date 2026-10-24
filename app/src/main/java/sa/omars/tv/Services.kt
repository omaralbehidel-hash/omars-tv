package sa.omars.tv

import android.os.Handler
import android.os.Looper
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.Executors

val main = Handler(Looper.getMainLooper())
private val pool = Executors.newFixedThreadPool(2)

fun bg(work: () -> Unit) {
    pool.execute {
        try {
            work()
        } catch (_: Throwable) {
        }
    }
}

fun ui(work: () -> Unit) = main.post { work() }

fun httpGet(url: String, timeoutMs: Int = 12000): String? {
    var c: HttpURLConnection? = null
    return try {
        c = URL(url).openConnection() as HttpURLConnection
        c.connectTimeout = timeoutMs
        c.readTimeout = timeoutMs
        c.setRequestProperty("Accept", "application/json")
        if (c.responseCode != 200) null else c.inputStream.bufferedReader().use { it.readText() }
    } catch (_: Exception) {
        null
    } finally {
        c?.disconnect()
    }
}

private const val AR_DIGITS = "٠١٢٣٤٥٦٧٨٩"
fun arNum(n: Int, digits: Boolean): String = arStr(n.toString(), digits)
fun arStr(s: String, digits: Boolean): String {
    if (!digits) return s
    val sb = StringBuilder()
    for (ch in s) sb.append(if (ch in '0'..'9') AR_DIGITS[ch - '0'] else ch)
    return sb.toString()
}

fun p2(n: Int) = if (n < 10) "0$n" else "$n"

val DAYS = arrayOf("الاثنين", "الثلاثاء", "الأربعاء", "الخميس", "الجمعة", "السبت", "الأحد")
val GMONTHS = arrayOf("يناير", "فبراير", "مارس", "أبريل", "مايو", "يونيو", "يوليو", "أغسطس", "سبتمبر", "أكتوبر", "نوفمبر", "ديسمبر")
val HMONTHS = arrayOf(
    "محرم", "صفر", "ربيع الأول", "ربيع الآخر", "جمادى الأولى", "جمادى الآخرة",
    "رجب", "شعبان", "رمضان", "شوال", "ذو القعدة", "ذو الحجة"
)

fun dayName(z: ZonedDateTime) = DAYS[z.dayOfWeek.value - 1]

fun gregorianText(z: ZonedDateTime, digits: Boolean) =
    arStr("${z.dayOfMonth} ${GMONTHS[z.monthValue - 1]} ${z.year}", digits)

fun hijriText(z: ZonedDateTime, offset: Int, digits: Boolean): String {
    return try {
        val h = HijrahDate.from(z.toLocalDate().plusDays(offset.toLong()))
        arStr("${h.get(java.time.temporal.ChronoField.DAY_OF_MONTH)} ${HMONTHS[h.get(java.time.temporal.ChronoField.MONTH_OF_YEAR) - 1]} ${h.get(java.time.temporal.ChronoField.YEAR_OF_ERA)} هـ", digits)
    } catch (_: Exception) {
        ""
    }
}

/** ‏"٠٦:٥٥" ، مع صباحًا/مساءً عند نظام 12 ساعة. */
fun timeText(z: ZonedDateTime, h24: Boolean, digits: Boolean): Pair<String, String> {
    val h = z.hour
    val m = z.minute
    return if (h24) Pair(arStr("${p2(h)}:${p2(m)}", digits), "")
    else {
        val h12 = if (h % 12 == 0) 12 else h % 12
        Pair(arStr("${p2(h12)}:${p2(m)}", digits), if (h < 12) "ص" else "م")
    }
}

fun fmtTimeStr(secOfDay: Int, h24: Boolean, digits: Boolean): String {
    val h = secOfDay / 3600
    val m = (secOfDay % 3600) / 60
    return if (h24) arStr("${p2(h)}:${p2(m)}", digits)
    else {
        val h12 = if (h % 12 == 0) 12 else h % 12
        arStr("${p2(h12)}:${p2(m)}", digits) + (if (h < 12) " ص" else " م")
    }
}

val PRAYER_KEYS = arrayOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha")
val PRAYER_NAMES = arrayOf("الفجر", "الشروق", "الظهر", "العصر", "المغرب", "العشاء")

/** مواقيت يوم واحد: ست قيم بالثواني من منتصف الليل. */
object Prayer {
    private val fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.ENGLISH)

    fun key(c: City, d: LocalDate) =
        "pt.${"%.2f".format(Locale.ENGLISH, c.lat)}.${"%.2f".format(Locale.ENGLISH, c.lon)}.${Store.s.method}.${Store.s.asr}.${d.format(fmt)}"

    fun cached(c: City, d: LocalDate): IntArray? {
        val js = Store.getStr(key(c, d))
        if (js.isEmpty()) return null
        return try {
            val a = org.json.JSONArray(js)
            IntArray(6) { a.getInt(it) }
        } catch (_: Exception) {
            null
        }
    }

    /** يُستدعى في خيط خلفي. */
    fun fetch(c: City, d: LocalDate): IntArray? {
        cached(c, d)?.let { return it }
        val url = "https://api.aladhan.com/v1/timings/${d.format(fmt)}?latitude=${c.lat}&longitude=${c.lon}&method=${Store.s.method}&school=${Store.s.asr}"
        val body = httpGet(url) ?: return null
        return try {
            val t = JSONObject(body).getJSONObject("data").getJSONObject("timings")
            val out = IntArray(6)
            for (i in 0 until 6) {
                val m = Regex("(\\d{1,2}):(\\d{2})").find(t.getString(PRAYER_KEYS[i])) ?: return null
                out[i] = m.groupValues[1].toInt() * 3600 + m.groupValues[2].toInt() * 60
            }
            Store.putStr(key(c, d), org.json.JSONArray(out.toList()).toString())
            out
        } catch (_: Exception) {
            null
        }
    }
}

data class Weather(val temp: Double, val hum: Int, val code: Int, val wind: Double, val at: Long)

object Wx {
    fun fetch(c: City): Weather? {
        val url = "https://api.open-meteo.com/v1/forecast?latitude=${c.lat}&longitude=${c.lon}&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=auto"
        val body = httpGet(url) ?: return null
        return try {
            val cu = JSONObject(body).getJSONObject("current")
            Weather(cu.getDouble("temperature_2m"), cu.getDouble("relative_humidity_2m").toInt(), cu.getInt("weather_code"), cu.getDouble("wind_speed_10m"), System.currentTimeMillis())
        } catch (_: Exception) {
            null
        }
    }

    fun text(code: Int): String = when (code) {
        0 -> "صافٍ"
        1 -> "صافٍ غالبًا"
        2 -> "غائم جزئيًا"
        3 -> "غائم"
        45, 48 -> "ضباب"
        51, 53, 55, 56, 57 -> "رذاذ"
        61, 63, 65, 66, 67 -> "مطر"
        71, 73, 75, 77 -> "ثلج"
        80, 81, 82 -> "زخات مطر"
        85, 86 -> "زخات ثلج"
        95, 96, 99 -> "عاصفة رعدية"
        else -> "—"
    }

    fun symbol(code: Int): String = when (code) {
        0, 1 -> "☀"
        2 -> "⛅"
        3 -> "☁"
        45, 48 -> "🌫"
        51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82 -> "🌧"
        71, 73, 75, 77, 85, 86 -> "❄"
        95, 96, 99 -> "⛈"
        else -> "•"
    }
}

fun zoneNow(c: City): ZonedDateTime = try {
    ZonedDateTime.now(ZoneId.of(c.tz))
} catch (_: Exception) {
    ZonedDateTime.now()
}
