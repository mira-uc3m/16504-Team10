package com.example.homebase.data.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.homebase.data.model.ClassActivity
import java.time.LocalDate
import java.time.YearMonth

class ScheduleViewModel : ViewModel() {
    var selectedDate by mutableStateOf(LocalDate.now())
    var currentMonth by mutableStateOf(YearMonth.now())

    // Dummy data to simulate the Figma --> will be replaced by stored data
    val allActivities = mutableStateListOf(
        ClassActivity("1", "Mobile Applications", "4.0.G01", "9:00 h", LocalDate.now(), 0xFF3F51B5),
        ClassActivity("2", "Class 2", "1.0.D01", "11:00 h", LocalDate.now().plusDays(2), 0xFFE91E63)
    )

    // Filter activities based on the selected date
    val filteredActivities: List<ClassActivity>
        get() = allActivities.filter { it.date == selectedDate }

    // Helper to check if a date has any activity (to show the dot)
    fun hasActivity(date: LocalDate): Boolean {
        return allActivities.any { it.date == date }
    }

    fun nextMonth() { currentMonth = currentMonth.plusMonths(1) }
    fun previousMonth() { currentMonth = currentMonth.minusMonths(1) }
}