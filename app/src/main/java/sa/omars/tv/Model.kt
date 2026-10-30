package sa.omars.tv

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.TimeZone

/** قناة أو محطة. t = yt | hls | audio ، و fb = بث رسمي احتياطي للقنوات التي أصلها يوتيوب. */
data class Channel(
    var n: String,
    var c: String,
    var u: String,
    var t: String,
    var fb: String = "",
    var f: Boolean = false
)

data class City(val id: String, val n: String, val lat: Double, val lon: Double, val tz: String)

val CITIES = listOf(
    City("madinah", "المدينة المنورة", 24.4686, 39.6142, "Asia/Riyadh"),
    City("makkah", "مكة المكرمة", 21.3891, 39.8579, "Asia/Riyadh"),
    City("jeddah", "جدة", 21.4858, 39.1925, "Asia/Riyadh"),
    City("riyadh", "الرياض", 24.7136, 46.6753, "Asia/Riyadh"),
    City("dammam", "الدمام", 26.4207, 50.0888, "Asia/Riyadh"),
    City("abha", "أبها", 18.2164, 42.5053, "Asia/Riyadh"),
    City("tabuk", "تبوك", 28.3838, 36.555, "Asia/Riyadh"),
    City("buraidah", "بريدة", 26.326, 43.975, "Asia/Riyadh"),
    City("dubai", "دبي", 25.2048, 55.2708, "Asia/Dubai"),
    City("kuwait", "الكويت", 29.3759, 47.9774, "Asia/Kuwait"),
    City("doha", "الدوحة", 25.2854, 51.531, "Asia/Qatar"),
    City("amman", "عمّان", 31.9454, 35.9284, "Asia/Amman"),
    City("cairo", "القاهرة", 30.0444, 31.2357, "Africa/Cairo"),
    City("gps", "موقعي الحالي", 0.0, 0.0, TimeZone.getDefault().id)
)

val METHODS = listOf(
    4 to "أم القرى", 3 to "رابطة العالم الإسلامي", 5 to "الهيئة المصرية", 2 to "ISNA", 1 to "جامعة العلوم – كراتشي"
)

private const val YTB = "https://www.youtube.com/channel/"
private const val K = "https://live.kwikmotion.com/"
private const val Q = "https://qurango.net/radio/"

object Defaults {
    fun tv(): MutableList<Channel> = mutableListOf(
        Channel("القرآن الكريم", "إسلامية", YTB + "UCos52azQNBgW63_9uDJoPDA", "yt", "https://cdn-globecast.akamaized.net/live/eds/saudi_quran/hls_roku/index.m3u8"),
        Channel("السنة النبوية", "إسلامية", YTB + "UCNF8YJDral7UmSisQ5XZMGw", "yt", "https://cdn-globecast.akamaized.net/live/eds/saudi_sunnah/hls_roku/index.m3u8"),
        Channel("الجزيرة", "أخبار", YTB + "UCfiwzLy-8yKzIbsmZTzxDgw", "yt", "https://live-hls-web-aja.getaj.net/AJA/index.m3u8"),
        Channel("الجزيرة 2", "أخبار", "https://live-hls-web-aja2-gcp.thehlive.com/AJA2/index.m3u8", "hls"),
        Channel("العربية", "أخبار", YTB + "UCrj5BGAhtWxDfqbza9T9hqA", "yt", "https://av.alarabiya.net/alarabiapublish/alarabiya.smil/playlist.m3u8"),
        Channel("الحدث", "أخبار", YTB + "UCYcogyJ0RBPHvfw3X-5L0bQ", "yt", "https://av.alarabiya.net/alarabiapublish/alhadath.smil/playlist.m3u8"),
        Channel("سكاي نيوز عربية", "أخبار", YTB + "UCIJXOvggjKtCagMfxvcCzAA", "yt", "https://stream.skynewsarabia.com/hls/sna.m3u8"),
        Channel("السعودية", "عامة", YTB + "UCPpsM0VmD3nuj915nMydkWQ", "yt"),
        Channel("الإخبارية السعودية", "أخبار", "", "yt"),
        Channel("الإخبارية السورية", "أخبار", "https://www.youtube.com/@AlekhbariahSY", "yt"),
        Channel("اقرأ", "إسلامية", YTB + "UCiC_h4iC9s7Ddr3CT82_EpA", "yt"),
        Channel("الرسالة", "إسلامية", YTB + "UC4CcTYIfVFoOqsFd-IAZfDw", "yt")
    )

    fun radio(): MutableList<Channel> = mutableListOf(
        Channel("إذاعة نداء الإسلام", "إذاعة", K + "sbrksanedaradiolive/ksanedaradio/playlist.m3u8", "audio"),
        Channel("إذاعة القرآن الكريم (السعودية)", "إذاعة", K + "sbrksaquranradiolive/ksaquranradio/playlist.m3u8", "audio"),
        Channel("إذاعة الرياض", "إذاعة", K + "sbrksariyadhradiolive/ksariyadhradio/playlist.m3u8", "audio"),
        Channel("إذاعة جدة", "إذاعة", K + "sbrksajeddahradiolive/ksajeddahradio/playlist.m3u8", "audio"),
        Channel("إذاعة الإخبارية", "إذاعة", K + "sbrksaikhbariyaradiolive/srpikhbariyaradio/playlist.m3u8", "audio"),
        Channel("ترتيل متواصل", "إذاعة", Q + "tarateel", "audio"),
        Channel("ماهر المعيقلي", "إذاعة", Q + "maher_almuaiqly", "audio"),
        Channel("ياسر الدوسري", "إذاعة", Q + "yasser_aldosari", "audio"),
        Channel("فارس عباد", "إذاعة", Q + "fares_abbad", "audio"),
        Channel("عبدالباسط عبدالصمد", "إذاعة", Q + "abdulbasit_abdulsamad", "audio"),
        Channel("تفسير القرآن", "إذاعة", Q + "tafseer", "audio")
    )
}

/** الإعدادات — نفس خيارات نسخة الويب. */
class Settings {
    var h24 = false
    var secs = true
    var date = true
    var wx = true
    var prayers = true
    var list = true
    var listLeft = true
    var fx = false
    var burn = false
    var digits = true
    var autoplay = false
    var alert = true
    var beep = false
    var sleepFx = false
    var boot = false
    var direct = true       // تفضيل البث الرسمي المباشر (أخف) على يوتيوب
    var clock = 1.0
    var listw = 20
    var pscale = 1.0
    var vscale = 1.0
    var dim = 0.25
    var hijri = 0
    var vol = 80
    var q = "med"
    var acc = "gold"
    var city = "madinah"
    var unit = "c"
    var method = 4
    var asr = 0
    var gpsLat = 0.0
    var gpsLon = 0.0

    fun toJson(): String {
        val o = JSONObject()
        o.put("h24", h24); o.put("secs", secs); o.put("date", date); o.put("wx", wx); o.put("prayers", prayers)
        o.put("list", list); o.put("listLeft", listLeft); o.put("fx", fx); o.put("burn", burn); o.put("digits", digits)
        o.put("autoplay", autoplay); o.put("alert", alert); o.put("beep", beep); o.put("sleepFx", sleepFx); o.put("boot", boot)
        o.put("src2", direct); o.put("clock", clock); o.put("listw", listw); o.put("pscale", pscale); o.put("vscale", vscale); o.put("dim", dim); o.put("hijri", hijri)
        o.put("vol", vol); o.put("q", q); o.put("acc", acc); o.put("city", city); o.put("unit", unit)
        o.put("method", method); o.put("asr", asr); o.put("gpsLat", gpsLat); o.put("gpsLon", gpsLon)
        return o.toString()
    }

    fun load(js: String?) {
        if (js.isNullOrEmpty()) return
        try {
            val o = JSONObject(js)
            h24 = o.optBoolean("h24", h24); secs = o.optBoolean("secs", secs); date = o.optBoolean("date", date)
            wx = o.optBoolean("wx", wx); prayers = o.optBoolean("prayers", prayers); list = o.optBoolean("list", list)
            listLeft = o.optBoolean("listLeft", listLeft); fx = o.optBoolean("fx", fx); burn = o.optBoolean("burn", burn)
            digits = o.optBoolean("digits", digits); autoplay = o.optBoolean("autoplay", autoplay); alert = o.optBoolean("alert", alert)
            beep = o.optBoolean("beep", beep); sleepFx = o.optBoolean("sleepFx", sleepFx); boot = o.optBoolean("boot", boot)
            direct = o.optBoolean("src2", direct); clock = o.optDouble("clock", clock); listw = o.optInt("listw", listw); pscale = o.optDouble("pscale", pscale); vscale = o.optDouble("vscale", vscale)
            dim = o.optDouble("dim", dim); hijri = o.optInt("hijri", hijri); vol = o.optInt("vol", vol)
            q = o.optString("q", q); acc = o.optString("acc", acc); city = o.optString("city", city); unit = o.optString("unit", unit)
            method = o.optInt("method", method); asr = o.optInt("asr", asr)
            gpsLat = o.optDouble("gpsLat", gpsLat); gpsLon = o.optDouble("gpsLon", gpsLon)
        } catch (_: Exception) {
        }
    }

    fun copyFrom(o: Settings) = load(o.toJson())

    fun currentCity(): City {
        val c = CITIES.firstOrNull { it.id == city } ?: CITIES[0]
        return if (c.id == "gps") {
            if (gpsLat != 0.0 || gpsLon != 0.0) City("gps", c.n, gpsLat, gpsLon, c.tz) else CITIES[0]
        } else c
    }
}

object Store {
    lateinit var sp: SharedPreferences
    val s = Settings()
    var tv: MutableList<Channel> = Defaults.tv()
    var radio: MutableList<Channel> = Defaults.radio()

    fun init(ctx: Context) {
        sp = ctx.getSharedPreferences("omars2", Context.MODE_PRIVATE)
        s.load(sp.getString("settings", null))
        tv = readList("tv") ?: Defaults.tv()
        radio = readList("radio") ?: Defaults.radio()
        if (sp.getInt("defv", 0) < 2) {
            for (d in Defaults.tv()) if (tv.none { it.n == d.n }) tv.add(d)
            sp.edit().putInt("defv", 2).apply()
            saveLists()
        }
    }

    fun saveSettings() = sp.edit().putString("settings", s.toJson()).apply()

    fun saveLists() {
        sp.edit().putString("tv", writeList(tv)).putString("radio", writeList(radio)).apply()
    }

    fun list(mode: String) = if (mode == "radio") radio else tv

    fun getStr(k: String, d: String = ""): String = sp.getString(k, d) ?: d
    fun putStr(k: String, v: String) = sp.edit().putString(k, v).apply()
    fun getInt(k: String, d: Int = 0) = sp.getInt(k, d)
    fun putInt(k: String, v: Int) = sp.edit().putInt(k, v).apply()

    private fun writeList(l: List<Channel>): String {
        val a = JSONArray()
        for (c in l) {
            val o = JSONObject()
            o.put("n", c.n); o.put("c", c.c); o.put("u", c.u); o.put("t", c.t); o.put("fb", c.fb); o.put("f", c.f)
            a.put(o)
        }
        return a.toString()
    }

    private fun readList(k: String): MutableList<Channel>? {
        val js = sp.getString(k, null) ?: return null
        return try {
            val a = JSONArray(js)
            val out = mutableListOf<Channel>()
            for (i in 0 until a.length()) {
                val o = a.getJSONObject(i)
                out.add(Channel(o.optString("n"), o.optString("c"), o.optString("u"), o.optString("t", "hls"), o.optString("fb"), o.optBoolean("f")))
            }
            if (out.isEmpty()) null else out
        } catch (_: Exception) {
            null
        }
    }
}
