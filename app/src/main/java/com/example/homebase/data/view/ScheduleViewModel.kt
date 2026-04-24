package com.example.homebase.data.view

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.homebase.NotificationHelper
import com.example.homebase.data.model.ClassActivity
import java.time.LocalDate
import java.time.YearMonth

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {
    private val notificationHelper = NotificationHelper(application)
    
    var selectedDate by mutableStateOf(LocalDate.now())
    var currentMonth by mutableStateOf(YearMonth.now())

    // Populated with Monday's classes from UC3M Class List
    val allActivities = mutableStateListOf(
        ClassActivity("1", "Mobile Applications", "53461", "9:00 h", LocalDate.now(), 0xFF3022A6),
        ClassActivity("2", "Computer Architecture", "12345", "11:00 h", LocalDate.now(), 0xFFFF4D4D),
        ClassActivity("3", "Operating Systems", "67890", "13:00 h", LocalDate.now(), 0xFF2196F3)
    )

    init {
        // Trigger a real notification when the app loads or data is ready
        checkAndSendReminders()
    }

    private fun checkAndSendReminders() {
        val today = LocalDate.now()
        val todaysClasses = allActivities.filter { it.date == today }
        
        todaysClasses.forEach { classActivity ->
            notificationHelper.sendClassReminderNotification(
                classActivity.name,
                classActivity.time
            )
        }
    }

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
