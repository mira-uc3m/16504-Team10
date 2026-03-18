package com.example.homebase.data.view

import androidx.compose.runtime.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import com.example.homebase.data.model.DateEntry

class AddScheduleViewModel : ViewModel() {
    var className by mutableStateOf("")
    var location by mutableStateOf("")
    var selectedColor by mutableStateOf(0xFF3F51B5)
    var selectedIcon by mutableStateOf(Icons.Default.Stars)

    val dateEntries = mutableStateListOf(DateEntry())

    fun addDateRow() {
        dateEntries.add(DateEntry())
    }
}