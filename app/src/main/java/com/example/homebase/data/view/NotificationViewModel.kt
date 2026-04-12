package com.example.homebase.data.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.homebase.data.model.Notification
import com.example.homebase.data.repository.NotificationRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationViewModel(
    private val repository: NotificationRepository = NotificationRepository()
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications.asStateFlow()

    private val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    init {
        fetchNotifications()
    }

    private fun fetchNotifications() {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            repository.getNotificationsFlow(uid).collect {
                _notifications.value = it
            }
        }
    }
}