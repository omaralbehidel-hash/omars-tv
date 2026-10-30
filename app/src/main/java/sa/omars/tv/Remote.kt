package sa.omars.tv

import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder

/** خادم صغير داخل الشبكة المنزلية: يتيح إضافة القنوات من متصفح الهاتف بدل الكتابة بالريموت. يعمل فقط أثناء فتح «إدارة القنوات». */
object Remote {
    @Volatile private var server: ServerSocket? = null
    @Volatile var port = 0
    private var onAdd: ((tab: String, name: String, url: String) -> String)? = null

    fun localIp(): String? {
        try {
            for (ni in NetworkInterface.getNetworkInterfaces()) {
                if (!ni.isUp || ni.isLoopback) continue
                for (a in ni.inetAddresses) if (a is Inet4Address && !a.isLoopbackAddress) return a.hostAddress
            }
        } catch (_: Exception) {
        }
        return null
    }

    fun start(handler: (String, String, String) -> String): String? {
        onAdd = handler
        if (server != null) return address()
        for (p in 8080..8090) {
            try {
                val s = ServerSocket(p)
                server = s
                port = p
                Thread {
                    while (!s.isClosed) {
                        try {
                            val c = s.accept()
                            Thread { serve(c) }.start()
                        } catch (_: Exception) {
                            break
                        }
                    }
                }.start()
                return address()
            } catch (_: Exception) {
            }
        }
        return null
    }

    fun address(): String? = localIp()?.let { "http://$it:$port" }

    fun stop() {
        try { server?.close() } catch (_: Exception) {}
        server = null
        onAdd = null
    }

    private fun serve(c: Socket) {
        try {
            c.soTimeout = 8000
            val inp = c.getInputStream()
            val head = StringBuilder()
            var last4 = 0
            while (head.length < 16384) {
                val b = inp.read()
                if (b < 0) break
                head.append(b.toChar())
                last4 = ((last4 shl 8) or b) and 0x7fffffff
                if (head.endsWith("\r\n\r\n")) break
            }
            val lines = head.toString().split("\r\n")
            val req = lines.firstOrNull() ?: return
            var len = 0
            for (l in lines) if (l.startsWith("Content-Length:", true)) len = l.substringAfter(":").trim().toIntOrNull() ?: 0
            val out = c.getOutputStream()
            if (req.startsWith("POST /add")) {
                val buf = ByteArray(len.coerceAtMost(8192))
                var got = 0
                while (got < buf.size) {
                    val n = inp.read(buf, got, buf.size - got)
                    if (n < 0) break
                    got += n
                }
                val form = HashMap<String, String>()
                for (kv in String(buf, 0, got, Charsets.UTF_8).split("&")) {
                    val i = kv.indexOf('=')
                    if (i > 0) form[URLDecoder.decode(kv.substring(0, i), "UTF-8")] = URLDecoder.decode(kv.substring(i + 1), "UTF-8")
                }
                val name = form["name"]?.trim().orEmpty()
                val url = form["url"]?.trim().orEmpty()
                val tab = if (form["tab"] == "radio") "radio" else "tv"
                val msg = if (name.isEmpty()) "أدخل الاسم." else (onAdd?.invoke(tab, name, url) ?: "الخدمة متوقفة، افتح إدارة القنوات في التلفزيون.")
                reply(out, page(msg))
            } else {
                reply(out, page(""))
            }
            out.flush()
        } catch (_: Exception) {
        } finally {
            try { c.close() } catch (_: Exception) {}
        }
    }

    private fun reply(out: java.io.OutputStream, html: String) {
        val body = html.toByteArray(Charsets.UTF_8)
        out.write(("HTTP/1.1 200 OK\r\nContent-Type: text/html; charset=utf-8\r\nContent-Length: ${body.size}\r\nConnection: close\r\n\r\n").toByteArray())
        out.write(body)
    }

    private fun esc(s: String) = s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")

    private fun page(msg: String): String =
        """<!DOCTYPE html><html lang="ar" dir="rtl"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>OMAR'S – إضافة قناة</title><style>
body{margin:0;background:#000;color:#f5f5f7;font-family:system-ui,sans-serif;padding:20px}
h1{font-size:22px;color:#e3c58f;margin:0 0 6px}p{color:#a1a1a6;font-size:14px;line-height:1.7}
input,select,button{width:100%;box-sizing:border-box;font-size:17px;padding:14px;margin:8px 0;border-radius:14px;border:1px solid #33ffffff;background:#1c1c1e;color:#f5f5f7}
input[name=url]{direction:ltr;text-align:left}button{background:#e3c58f;color:#1d1608;font-weight:700;border:0}
.m{background:#2b2417;border-radius:12px;padding:12px;margin:10px 0;color:#e3c58f}</style></head><body>
<h1>OMAR'S – إضافة قناة</h1><p>الصق رابط يوتيوب (قناة أو بث مباشر أو @اسم_القناة) أو رابط بث مباشر (m3u8) أو رابط إذاعة.</p>
${if (msg.isNotEmpty()) "<div class=\"m\">" + esc(msg) + "</div>" else ""}
<form method="post" action="/add"><select name="tab"><option value="tv">قناة تلفزيونية</option><option value="radio">إذاعة</option></select>
<input name="name" placeholder="الاسم" required><input name="url" placeholder="https://…" inputmode="url" autocapitalize="off" required>
<button type="submit">إضافة إلى التلفزيون</button></form></body></html>"""
}
