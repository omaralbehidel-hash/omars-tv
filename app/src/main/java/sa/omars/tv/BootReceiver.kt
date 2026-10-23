package sa.omars.tv

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * يشغّل التطبيق عند إقلاع الجهاز إذا فعّل المستخدم الخيار من الإعدادات.
 * ملاحظة: بعض إصدارات Android TV (10+) تمنع بدء الواجهات من الخلفية، فقد لا يعمل على كل جهاز.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val on = context.getSharedPreferences(MainActivity.PREFS, Context.MODE_PRIVATE)
            .getBoolean(MainActivity.KEY_BOOT, false)
        if (!on) return
        val i = Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try { context.startActivity(i) } catch (_: Exception) { }
    }
}
