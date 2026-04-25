package com.example.homebase.data.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {
    var pushNotifications by mutableStateOf(true)
    var classReminders by mutableStateOf(false)
    var checklistReminders by mutableStateOf(true)
    var locationTrackingEnabled by mutableStateOf(false)

    fun toggleLocationTracking(enabled: Boolean) {
        locationTrackingEnabled = enabled
    }
}