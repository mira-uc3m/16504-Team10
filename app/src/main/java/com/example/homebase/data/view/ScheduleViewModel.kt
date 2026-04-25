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
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters

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
        val today = LocalDate.now()
        allActivities.filter { isEventOnDate(it, today) }
            .sortedBy { it.time }
    }

    init {
        addMockData()
        fetchScheduleFromFirebase()
    }

    private fun addMockData() {
        // Set start dates to early 2025 so they show up for all weeks
        val startDate = LocalDate.of(2025, 1, 1)
        val monday = startDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
        val tuesday = monday.plusDays(1)
        val wednesday = monday.plusDays(2)
        val thursday = monday.plusDays(3)
        val friday = monday.plusDays(4)

        val mockEvents = listOf(
            // Monday
            ScheduleEvent(
                id = "mock1",
                name = "Mobile Applications",
                location = "Room 402",
                time = "09:30 AM",
                date = monday.toString(),
                color = 0xFF3022A6L,
                iconIndex = 0,
                repeatType = "Weekly"
            ),
            ScheduleEvent(
                id = "mock2",
                name = "Operating Systems",
                location = "Lab 1.1",
                time = "11:00 AM",
                date = monday.toString(),
                color = 0xFFD85D6AL,
                iconIndex = 1,
                repeatType = "Weekly"
            ),
            ScheduleEvent(
                id = "mock3",
                name = "Software Engineering",
                location = "Auditorium A",
                time = "01:00 PM",
                date = monday.toString(),
                color = 0xFF42A5F5L,
                iconIndex = 2,
                repeatType = "Weekly"
            ),
            // Tuesday
            ScheduleEvent(
                id = "mock4",
                name = "Computer Networks",
                location = "Room 305",
                time = "09:30 AM",
                date = tuesday.toString(),
                color = 0xFFE9B25AL,
                iconIndex = 3,
                repeatType = "Weekly"
            ),
            ScheduleEvent(
                id = "mock5",
                name = "Database Systems",
                location = "Room 202",
                time = "11:30 AM",
                date = tuesday.toString(),
                color = 0xFF4DB6ACL,
                iconIndex = 4,
                repeatType = "Weekly"
            ),
            ScheduleEvent(
                id = "mock6",
                name = "Mobile Applications",
                location = "Room 402",
                time = "02:30 PM",
                date = tuesday.toString(),
                color = 0xFF3022A6L,
                iconIndex = 0,
                repeatType = "Weekly"
            ),
            // Wednesday
            ScheduleEvent(
                id = "mock7",
                name = "Software Engineering",
                location = "Auditorium A",
                time = "09:30 AM",
                date = wednesday.toString(),
                color = 0xFF42A5F5L,
                iconIndex = 2,
                repeatType = "Weekly"
            ),
            ScheduleEvent(
                id = "mock8",
                name = "Operating Systems",
                location = "Lab 1.1",
                time = "11:00 AM",
                date = wednesday.toString(),
                color = 0xFFD85D6AL,
                iconIndex = 1,
                repeatType = "Weekly"
            ),
            ScheduleEvent(
                id = "mock9",
                name = "Artificial Intelligence",
                location = "Room 101",
                time = "01:00 PM",
                date = wednesday.toString(),
                color = 0xFFE9B25AL,
                iconIndex = 3,
                repeatType = "Weekly"
            ),
            // Thursday
            ScheduleEvent(
                id = "mock10",
                name = "Computer Networks",
                location = "Room 305",
                time = "09:30 AM",
                date = thursday.toString(),
                color = 0xFFE9B25AL,
                iconIndex = 3,
                repeatType = "Weekly"
            ),
            ScheduleEvent(
                id = "mock11",
                name = "Spanish Language",
                location = "Room 501",
                time = "11:30 AM",
                date = thursday.toString(),
                color = 0xFF81C784L,
                iconIndex = 4,
                repeatType = "Weekly"
            ),
            ScheduleEvent(
                id = "mock12",
                name = "Ethics in Engineering",
                location = "Room 205",
                time = "02:00 PM",
                date = thursday.toString(),
                color = 0xFF3022A6L,
                iconIndex = 0,
                repeatType = "Weekly"
            ),
            // Friday
            ScheduleEvent(
                id = "mock13",
                name = "Distributed Systems",
                location = "Lab 2.3",
                time = "10:00 AM",
                date = friday.toString(),
                color = 0xFF4DB6ACL,
                iconIndex = 5,
                repeatType = "Weekly"
            ),
            ScheduleEvent(
                id = "mock14",
                name = "Capstone Project",
                location = "Meeting Room",
                time = "01:00 PM",
                date = friday.toString(),
                color = 0xFFD85D6AL,
                iconIndex = 1,
                repeatType = "Weekly"
            )
        )
        allActivities.addAll(mockEvents)
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
                notificationRepository.deleteNotificationByEventId(event.id)
                fetchScheduleFromFirebase()
            } else if (event.id.startsWith("mock")) {
                allActivities.remove(event)
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
                    if (!event.id.startsWith("mock")) {
                        repository.postEvent(updatedEvent)
                        fetchScheduleFromFirebase()
                    } else {
                        allActivities.remove(event)
                        allActivities.add(updatedEvent)
                    }
                }
            } else {
                val beforeEndDate = date.minusDays(1).toString()
                val afterStartDate = when (event.repeatType) {
                    "Daily" -> date.plusDays(1)
                    "Weekly" -> date.plusWeeks(1)
                    else -> date.plusDays(1)
                }

                val updatedOriginal = event.copy(endDate = beforeEndDate)
                
                if (!event.id.startsWith("mock")) {
                    repository.postEvent(updatedOriginal)
                } else {
                    allActivities.remove(event)
                    allActivities.add(updatedOriginal)
                }

                if (event.endDate == null || afterStartDate.isBefore(LocalDate.parse(event.endDate).plusDays(1))) {
                    val newEvent = event.copy(
                        id = if (event.id.startsWith("mock")) "mock_" + java.util.UUID.randomUUID().toString() else java.util.UUID.randomUUID().toString(),
                        date = afterStartDate.toString()
                    )
                    if (!event.id.startsWith("mock")) {
                        repository.postEvent(newEvent)
                    } else {
                        allActivities.add(newEvent)
                    }
                }
                
                if (!event.id.startsWith("mock")) {
                    fetchScheduleFromFirebase()
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