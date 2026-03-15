package com.example.homebase.data.view

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.homebase.data.model.ClassActivity
import java.time.LocalDate

class ScheduleViewModel : ViewModel() {
    // Current selected date in the calendar
    var selectedDate by mutableStateOf(LocalDate.now())

    // Dummy data to simulate your screenshot
    val allActivities = listOf(
        ClassActivity("1", "Mobile Applications", "4.0.G01", "9:00 h", LocalDate.of(2025, 9, 10), 0xFF3F51B5),
        ClassActivity("2", "Class 2", "1.0.D01", "11:00 h", LocalDate.of(2025, 9, 10), 0xFFE91E63),
        ClassActivity("3", "Class 3", "4.0.D04", "13:00 h", LocalDate.of(2025, 9, 10), 0xFF2196F3),
        ClassActivity("1", "Mobile Applications", "4.0.G01", "9:00 h", LocalDate.of(2025, 9, 12), 0xFF3F51B5)
    )

    // Filter activities based on the selected date
    val filteredActivities: List<ClassActivity>
        get() = allActivities.filter { it.date == selectedDate }

    // Helper to check if a date has any activity (to show the dot)
    fun hasActivity(date: LocalDate): Boolean {
        return allActivities.any { it.date == date }
    }
}