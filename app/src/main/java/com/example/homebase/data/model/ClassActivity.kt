package com.example.homebase.data.model

import java.time.LocalDate

data class ClassActivity(
    val id: String,
    val name: String,
    val room: String,
    val time: String,
    val date: LocalDate,
    val color: Long // Hex color for the icon background
)