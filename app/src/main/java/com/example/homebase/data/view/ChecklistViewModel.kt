package com.example.homebase.data.view

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class ChecklistItem(
    val id: Int,
    val title: String,
    val isDone: Boolean = false,
    val category: String = "This Week" // "This Week" or "Upcoming"
)

class ChecklistViewModel : ViewModel() {
    private val _items = mutableStateListOf<ChecklistItem>()
    val items: List<ChecklistItem> get() = _items

    init {
        // Initial data matching the user's Figma/request
        loadFallTerm()
    }

    fun loadFallTerm() {
        _items.clear()
        _items.addAll(listOf(
            ChecklistItem(1, "Visa", false, "This Week"),
            ChecklistItem(2, "Flight", false, "This Week"),
            ChecklistItem(3, "Accommodation", false, "This Week"),
            ChecklistItem(4, "School Enrollment", false, "This Week"),
            ChecklistItem(5, "Learn Route to School", false, "This Week"),
            ChecklistItem(6, "Pay Rent", false, "This Week"),
            ChecklistItem(7, "Plan Travel", false, "Upcoming"),
            ChecklistItem(8, "Get Metro Card", false, "Upcoming")
        ))
    }

    fun loadWinterTerm() {
        _items.clear()
        _items.addAll(listOf(
            ChecklistItem(9, "Winter Visa Extension", false, "This Week"),
            ChecklistItem(10, "Warm Clothing Shopping", false, "This Week"),
            ChecklistItem(11, "Winter Housing Check", false, "This Week"),
            ChecklistItem(12, "Health Insurance Renewal", false, "Upcoming"),
            ChecklistItem(13, "Holiday Travel Planning", false, "Upcoming")
        ))
    }

    fun toggleItem(id: Int) {
        val index = _items.indexOfFirst { it.id == id }
        if (index != -1) {
            val item = _items[index]
            _items[index] = item.copy(isDone = !item.isDone)
        }
    }

    fun addItem(title: String, category: String) {
        val newId = (_items.maxOfOrNull { it.id } ?: 0) + 1
        _items.add(ChecklistItem(newId, title, false, category))
    }

    fun getProgress(): Float {
        if (_items.isEmpty()) return 0f
        val completed = _items.count { it.isDone }
        return completed.toFloat() / _items.size
    }
}
