package com.example.homebase.data.view

import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import com.example.homebase.data.model.DateEntry

class AddScheduleViewModel : ViewModel() {
    var className by mutableStateOf("")
    var location by mutableStateOf("")
    var selectedColor by mutableStateOf(0xFF332CA4L)
    var selectedIconIndex by mutableStateOf(0)

    val icons = listOf(
        Icons.Default.Stars,
        Icons.Default.Thunderstorm,
        Icons.Default.Notes,
        Icons.Default.Train,
        Icons.Default.List,
        Icons.Default.School // Adding more if needed
    )

    val dateEntries = mutableStateListOf(DateEntry())

    fun addDateRow() {
        dateEntries.add(DateEntry())
    }
}