package com.example.homebase.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val eventId = intent.getStringExtra("EVENT_ID") ?: "unknown"
        val eventName = intent.getStringExtra("EVENT_NAME") ?: "Upcoming Lecture"
        val eventLocation = intent.getStringExtra("EVENT_LOCATION") ?: ""
        
        Log.d("AlarmReceiver", "Alarm triggered for event: $eventName (ID: $eventId) at $eventLocation")
        
        val notificationHelper = NotificationHelper(context)
        notificationHelper.showNotification(
            title = eventName,
            message = "Class starting at $eventLocation"
        )
    }
}