package com.example.homebase.data.repository

import android.util.Log
import com.example.homebase.data.model.Notification
import com.example.homebase.data.model.ScheduleEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class NotificationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val notificationsCollection = db.collection("notifications")

    fun getNotificationsFlow(userId: String): Flow<List<Notification>> = callbackFlow {
        val subscription = notificationsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("NotificationRepository", "Firestore error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val notifications = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Notification::class.java)
                } ?: emptyList()

                trySend(notifications.sortedByDescending { it.timestamp })
            }

        awaitClose { subscription.remove() }
    }

    suspend fun postNotification(notification: Notification): Boolean {
        return try {
            notificationsCollection.document(notification.id).set(notification).await()
            true
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error posting notification: ${e.message}")
            false
        }
    }

    suspend fun deleteNotification(notificationId: String): Boolean {
        return try {
            notificationsCollection.document(notificationId).delete().await()
            true
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error deleting notification: ${e.message}")
            false
        }
    }

    suspend fun deleteNotificationByEventId(eventId: String): Boolean {
        return deleteNotification("notif_$eventId")
    }

    fun createNotificationFromEvent(event: ScheduleEvent, userId: String): Notification? {
        return try {
            val eventDate = LocalDate.parse(event.date)
            val eventTime = try {
                LocalTime.parse(event.time)
            } catch (e: Exception) {
                // Legacy fallback for AM/PM format
                LocalTime.parse(event.time, DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH))
            }
            
            val eventDateTime = LocalDateTime.of(eventDate, eventTime)
            val now = LocalDateTime.now()
            
            if (eventDateTime.isAfter(now.minusHours(1))) {
                val minutesUntil = ChronoUnit.MINUTES.between(now, eventDateTime)
                
                val status = when {
                    minutesUntil < 0 -> "Started"
                    minutesUntil <= 5 -> "Starts in $minutesUntil min"
                    else -> "Upcoming"
                }

                // Standardize display time to 12-hour format
                val displayTime = eventDateTime.format(DateTimeFormatter.ofPattern("MMM dd, hh:mm a", Locale.ENGLISH))

                Notification(
                    id = "notif_${event.id}",
                    userId = userId,
                    title = event.name,
                    time = displayTime,
                    status = status,
                    room = event.location,
                    timestamp = eventDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                )
            } else null
        } catch (e: Exception) {
            Log.e("NotificationRepository", "Error creating notification object: ${e.message}")
            null
        }
    }
}