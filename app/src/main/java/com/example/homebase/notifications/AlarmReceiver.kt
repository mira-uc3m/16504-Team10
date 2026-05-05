package com.example.homebase.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val eventName = intent.getStringExtra("EVENT_NAME") ?: "Upcoming Lecture"
        val eventLocation = intent.getStringExtra("EVENT_LOCATION") ?: ""
        
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(
            title = eventName,
            message = "Class starting at $eventLocation"
        )
    }
}