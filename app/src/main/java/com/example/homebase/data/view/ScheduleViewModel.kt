package com.example.homebase.data.view

import android.app.Application
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.homebase.data.model.ScheduleEvent
import com.example.homebase.data.repository.NotificationRepository
import com.example.homebase.data.repository.ScheduleRepository
import com.example.homebase.notifications.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ScheduleRepository = ScheduleRepository()
    private val notificationRepository: NotificationRepository = NotificationRepository()
    private val notificationHelper = NotificationHelper(application)
    
    var selectedDate by mutableStateOf(LocalDate.now())
    var currentMonth by mutableStateOf(YearMonth.now())

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    var allActivities = mutableStateListOf<ScheduleEvent>()
    var isLoading = mutableStateOf(false)

    val todayClasses = derivedStateOf {
        val today = LocalDate.now()
        allActivities.filter { isEventOnDate(it, today) }
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
                events.forEach { event ->
                    if (allActivities.none { it.id == event.id }) {
                        allActivities.add(event)
                        notificationHelper.scheduleExactAlarm(event)
                    }
                }
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
                    notificationHelper.scheduleExactAlarm(event)
                    val notification = notificationRepository.createNotificationFromEvent(event, uid)
                    notification?.let {
                        notificationRepository.postNotification(it)
                    }
                }
            }
            fetchScheduleFromFirebase()
        }
    }

    fun deleteEvent(event: ScheduleEvent) {
        viewModelScope.launch {
            val success = repository.deleteEvent(event.id)
            if (success) {
                notificationHelper.cancelAlarm(event)
                notificationRepository.deleteNotificationByEventId(event.id)
                fetchScheduleFromFirebase()
            }
        }
    }

    fun deleteSingleInstance(event: ScheduleEvent, date: LocalDate) {
        viewModelScope.launch {
            val eventStartDate = LocalDate.parse(event.date)
            
            if (date == eventStartDate) {
                val newStartDate = when (event.repeatType) {
                    "Daily" -> date.plusDays(1)
                    "Weekly" -> date.plusWeeks(1)
                    else -> date.plusDays(1)
                }
                
                if (event.endDate != null && newStartDate.isAfter(LocalDate.parse(event.endDate))) {
                    deleteEvent(event)
                } else {
                    val updatedEvent = event.copy(date = newStartDate.toString())
                    allActivities.remove(event)
                    allActivities.add(updatedEvent)
                    notificationHelper.scheduleExactAlarm(updatedEvent)
                }
            } else {
                val beforeEndDate = date.minusDays(1).toString()
                val afterStartDate = when (event.repeatType) {
                    "Daily" -> date.plusDays(1)
                    "Weekly" -> date.plusWeeks(1)
                    else -> date.plusDays(1)
                }

                val updatedOriginal = event.copy(endDate = beforeEndDate)
                
                allActivities.remove(event)
                allActivities.add(updatedOriginal)
                notificationHelper.scheduleExactAlarm(updatedOriginal)

                if (event.endDate == null || afterStartDate.isBefore(LocalDate.parse(event.endDate).plusDays(1))) {
                    val newEvent = event.copy(
                        id = java.util.UUID.randomUUID().toString(),
                        date = afterStartDate.toString()
                    )
                    allActivities.add(newEvent)
                    notificationHelper.scheduleExactAlarm(newEvent)
                }
            }
        }
    }

    fun isEventOnDate(event: ScheduleEvent, targetDate: LocalDate): Boolean {
        return try {
            val eventDate = LocalDate.parse(event.date)
            if (targetDate.isBefore(eventDate)) return false
            if (event.endDate != null && targetDate.isAfter(LocalDate.parse(event.endDate))) return false
            
            when (event.repeatType) {
                "Never" -> targetDate == eventDate
                "Daily" -> true
                "Weekly" -> targetDate.dayOfWeek == eventDate.dayOfWeek
                else -> targetDate == eventDate
            }
        } catch (e: Exception) {
            false
        }
    }

    val filteredActivities: List<ScheduleEvent>
        get() = allActivities.filter { isEventOnDate(it, selectedDate) }

    fun hasActivity(date: LocalDate): Boolean {
        return allActivities.any { isEventOnDate(it, date) }
    }

    fun nextMonth() { currentMonth = currentMonth.plusMonths(1) }
    fun previousMonth() { currentMonth = currentMonth.minusMonths(1) }
}