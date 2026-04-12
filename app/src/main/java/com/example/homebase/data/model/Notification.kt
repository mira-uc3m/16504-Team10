package com.example.homebase.data.model

data class Notification(
    val id: String = "",
    val title: String = "",
    val time: String = "",
    val status: String = "",
    val room: String = "",
    val timestamp: Long = System.currentTimeMillis()
)