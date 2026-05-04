package com.example.homebase.data.view

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    var pushNotifications by mutableStateOf(sharedPreferences.getBoolean("push_notifications", true))
    var classReminders by mutableStateOf(sharedPreferences.getBoolean("class_reminders", false))
    var checklistReminders by mutableStateOf(sharedPreferences.getBoolean("checklist_reminders", true))
    var locationTrackingEnabled by mutableStateOf(sharedPreferences.getBoolean("location_tracking", false))

    fun togglePushNotifications(enabled: Boolean) {
        pushNotifications = enabled
        sharedPreferences.edit().putBoolean("push_notifications", enabled).apply()
    }

    fun toggleClassReminders(enabled: Boolean) {
        classReminders = enabled
        sharedPreferences.edit().putBoolean("class_reminders", enabled).apply()
    }

    fun toggleChecklistReminders(enabled: Boolean) {
        checklistReminders = enabled
        sharedPreferences.edit().putBoolean("checklist_reminders", enabled).apply()
    }

    fun toggleLocationTracking(enabled: Boolean) {
        locationTrackingEnabled = enabled
        sharedPreferences.edit().putBoolean("location_tracking", enabled).apply()
    }
}