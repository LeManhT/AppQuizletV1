package com.example.quizletappandroidv1.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.quizletappandroidv1.utils.NotificationUtils

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            NotificationUtils.showNotification(context)
        }
    }
}