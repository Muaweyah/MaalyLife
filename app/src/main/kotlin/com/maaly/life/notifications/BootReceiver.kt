package com.maaly.life.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // مكان مستقبلي لإعادة جدولة كل التنبيهات المحفوظة بعد إعادة تشغيل الجهاز
    }
}
