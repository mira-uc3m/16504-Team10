package com.example.homebase.data.repository

import android.util.Log
import com.example.homebase.data.model.Notification
import com.example.homebase.data.model.ScheduleEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class NotificationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val eventsCollection = db.collection("scheduleEvents")

    fun getNotificationsFlow(): Flow<List<Notification>> = callbackFlow {
        val subscription = eventsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("NotificationRepository", "Firestore error: ${error.message}")
                trySend(emptyList()) // Avoid crashing, send empty list instead
                return@addSnapshotListener
            }

            val notifications = snapshot?.documents?.mapNotNull { doc ->
                try {
                    val event = doc.toObject(ScheduleEvent::class.java)
                    event?.let { createNotificationIfUpcoming(it) }
                } catch (e: Exception) {
                    Log.e("NotificationRepository", "Error parsing event: ${e.message}")
                    null
                }
            } ?: emptyList()

            trySend(notifications.sortedByDescending { it.timestamp })
        }

        awaitClose { subscription.remove() }
    }

    private fun createNotificationIfUpcoming(event: ScheduleEvent): Notification? {
        return try {
            // date: "YYYY-MM-DD", time: "HH:mm"
            val eventDateTime = LocalDateTime.parse("${event.date}T${event.time}:00")
            val now = LocalDateTime.now()
            
            // Only show notifications for events today or in the future
            if (eventDateTime.isAfter(now.minusHours(1))) {
                val minutesUntil = ChronoUnit.MINUTES.between(now, eventDateTime)
                
                val status = when {
                    minutesUntil < 0 -> "Started"
                    minutesUntil <= 5 -> "Starts in $minutesUntil min"
                    else -> "Upcoming"
                }

                Notification(
                    id = event.id,
                    title = event.name,
                    time = "${event.date} - ${event.time}h",
                    status = status,
                    room = event.location,
                    timestamp = eventDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                )
            } else null
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error creating notification: ${e.message}")
            null
        }
    }
}