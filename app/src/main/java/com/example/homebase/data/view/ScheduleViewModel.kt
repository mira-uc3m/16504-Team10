package com.example.homebase.data.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homebase.data.model.ClassActivity
import com.example.homebase.data.repository.ScheduleRepository
import com.example.homebase.data.model.ScheduleEvent
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class ScheduleViewModel(private val repository: ScheduleRepository = ScheduleRepository()) : ViewModel() {
    var selectedDate by mutableStateOf(LocalDate.now())
    var currentMonth by mutableStateOf(YearMonth.now())

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    // Dummy data to simulate the Figma --> will be replaced by stored data
    var allActivities = mutableStateListOf<ScheduleEvent>()
    var isLoading = mutableStateOf(false)

    init {
        // This is the "First Load" GET request
        fetchScheduleFromFirebase()
    }

    fun fetchScheduleFromFirebase() {
        val uid = currentUserId // Capture the ID

        if (uid != null) {
            viewModelScope.launch {
                isLoading.value = true
                val events = repository.getEvents(uid)
                allActivities.clear()
                allActivities.addAll(events)
                isLoading.value = false
            }
        } else {
            println("VIEWMODEL_ERROR: No user logged in")
        }
    }

    fun saveEventsToFirebase(events: List<ScheduleEvent>) {
        viewModelScope.launch {
            events.forEach { event ->
                repository.postEvent(event)
            }
            // Optional: Re-fetch to ensure UI is in sync
            fetchScheduleFromFirebase()
        }
    }

    // Filter activities based on the selected date
    val filteredActivities: List<ScheduleEvent>
        get() = allActivities.filter { LocalDate.parse(it.date) == selectedDate }

    // Helper to check if a date has any activity (to show the dot)
    fun hasActivity(date: LocalDate): Boolean {
        return allActivities.any { LocalDate.parse(it.date) == date }
    }

    fun nextMonth() { currentMonth = currentMonth.plusMonths(1) }
    fun previousMonth() { currentMonth = currentMonth.minusMonths(1) }
}