package com.example.homebase.data.model

data class ScheduleEvent(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val location: String = "",
    val time: String = "",
    val date: String = "", // Store as ISO String "YYYY-MM-DD"
    val color: Long = 0xFF3F51B5
)