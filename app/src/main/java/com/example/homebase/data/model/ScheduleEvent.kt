package com.example.homebase.data.model

data class ScheduleEvent(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val location: String = "",
    val time: String = "",
    val date: String = "", // Start date as ISO String "YYYY-MM-DD"
    val color: Long = 0xFF3F51B5,
    val iconIndex: Int = 0,
    val repeatType: String = "Never", // "Never", "Daily", "Weekly"
    val endDate: String? = null // Optional end date "YYYY-MM-DD"
)