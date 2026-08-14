package com.maaly.life.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "مهمة"
        val taskId = intent.getIntExtra("taskId", 0)
        NotificationHelper.show(context, taskId, title)
    }
}
