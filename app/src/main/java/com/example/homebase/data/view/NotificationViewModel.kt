package com.example.homebase.data.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homebase.data.model.Notification
import com.example.homebase.data.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class NotificationViewModel(
    private val repository: NotificationRepository = NotificationRepository()
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    init {
        fetchNotifications()
        // Periodically update statuses to keep them dynamic
        startStatusUpdateTimer()
    }

    private fun fetchNotifications() {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            repository.getNotificationsFlow(uid).collect { list ->
                val now = LocalDate.now()
                val filteredList = list.filter { notif ->
                    val notifDate = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(notif.timestamp),
                        ZoneId.systemDefault()
                    ).toLocalDate()
                    
                    val isPastDay = notifDate.isBefore(now)
                    if (isPastDay) {
                        // Automatically dismiss notifications from previous days
                        viewModelScope.launch {
                            repository.deleteNotification(notif.id)
                        }
                    }
                    !isPastDay
                }
                _notifications.value = filteredList.map { updateNotificationStatus(it) }
            }
        }
    }

    fun dismissNotification(notificationId: String) {
        viewModelScope.launch {
            repository.deleteNotification(notificationId)
        }
    }

    private fun startStatusUpdateTimer() {
        viewModelScope.launch {
            while (true) {
                delay(60000) // Update every minute
                _notifications.value = _notifications.value.map { updateNotificationStatus(it) }
            }
        }
    }

    private fun updateNotificationStatus(notification: Notification): Notification {
        val eventTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(notification.timestamp), ZoneId.systemDefault())
        val now = LocalDateTime.now()
        val minutesUntil = ChronoUnit.MINUTES.between(now, eventTime)

        val newStatus = when {
            minutesUntil < 0 -> "Started"
            minutesUntil <= 5 -> "Starts in $minutesUntil min"
            else -> "Upcoming"
        }

        return if (notification.status != newStatus) {
            notification.copy(status = newStatus)
        } else {
            notification
        }
    }
}