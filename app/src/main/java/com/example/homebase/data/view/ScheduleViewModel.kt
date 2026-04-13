package com.example.homebase.data.view

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homebase.data.model.ScheduleEvent
import com.example.homebase.data.repository.NotificationRepository
import com.example.homebase.data.repository.ScheduleRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class ScheduleViewModel(
    private val repository: ScheduleRepository = ScheduleRepository(),
    private val notificationRepository: NotificationRepository = NotificationRepository()
) : ViewModel() {
    var selectedDate by mutableStateOf(LocalDate.now())
    var currentMonth by mutableStateOf(YearMonth.now())

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    var allActivities = mutableStateListOf<ScheduleEvent>()
    var isLoading = mutableStateOf(false)

    val todayClasses = derivedStateOf {
        val todayStr = LocalDate.now().toString()
        allActivities.filter { it.date == todayStr }
            .sortedBy { it.time }
    }

    init {
        fetchScheduleFromFirebase()
    }

    fun fetchScheduleFromFirebase() {
        val uid = currentUserId
        if (uid != null) {
            viewModelScope.launch {
                isLoading.value = true
                val events = repository.getEvents(uid)
                allActivities.clear()
                allActivities.addAll(events)
                isLoading.value = false
            }
        }
    }

    fun saveEventsToFirebase(events: List<ScheduleEvent>) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            events.forEach { event ->
                val success = repository.postEvent(event)
                if (success) {
                    // Create and post a notification for this event
                    val notification = notificationRepository.createNotificationFromEvent(event, uid)
                    notification?.let {
                        notificationRepository.postNotification(it)
                    }
                }
            }
            fetchScheduleFromFirebase()
        }
    }

    val filteredActivities: List<ScheduleEvent>
        get() = allActivities.filter { 
            try { LocalDate.parse(it.date) == selectedDate } catch(e: Exception) { false }
        }

    fun hasActivity(date: LocalDate): Boolean {
        return allActivities.any { 
            try { LocalDate.parse(it.date) == date } catch(e: Exception) { false }
        }
    }

    fun nextMonth() { currentMonth = currentMonth.plusMonths(1) }
    fun previousMonth() { currentMonth = currentMonth.minusMonths(1) }
}