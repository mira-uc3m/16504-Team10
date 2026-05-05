package com.example.homebase.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.homebase.data.repository.ScheduleRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val repository = ScheduleRepository()
            val notificationHelper = NotificationHelper(context)

            CoroutineScope(Dispatchers.IO).launch {
                val events = repository.getEvents(userId)
                events.forEach { event ->
                    notificationHelper.scheduleExactAlarm(event)
                }
            }
        }
    }
}