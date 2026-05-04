package com.example.homebase.data.view

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ChecklistItem(
    val id: Int,
    val title: String,
    val isDone: Boolean = false,
    val category: String = "This Week" // "This Week" or "Upcoming"
)

class ChecklistViewModel(application: Application) : AndroidViewModel(application) {
    private val sharedPreferences = application.getSharedPreferences("checklist_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val _fallItems = mutableStateListOf<ChecklistItem>()
    private val _winterItems = mutableStateListOf<ChecklistItem>()
    
    private var currentTerm by mutableStateOf("Fall Term")

    val items: List<ChecklistItem>
        get() = if (currentTerm == "Fall Term") _fallItems else _winterItems

    init {
        loadSavedData()
    }

    private fun loadSavedData() {
        val fallJson = sharedPreferences.getString("fall_items", null)
        val winterJson = sharedPreferences.getString("winter_items", null)

        if (fallJson != null) {
            try {
                val type = object : TypeToken<List<ChecklistItem>>() {}.type
                val savedItems: List<ChecklistItem> = gson.fromJson(fallJson, type)
                _fallItems.addAll(savedItems)
            } catch (e: Exception) {
                _fallItems.addAll(getDefaultFallItems())
            }
        } else {
            _fallItems.addAll(getDefaultFallItems())
        }

        if (winterJson != null) {
            try {
                val type = object : TypeToken<List<ChecklistItem>>() {}.type
                val savedItems: List<ChecklistItem> = gson.fromJson(winterJson, type)
                _winterItems.addAll(savedItems)
            } catch (e: Exception) {
                _winterItems.addAll(getDefaultWinterItems())
            }
        } else {
            _winterItems.addAll(getDefaultWinterItems())
        }
    }

    private fun saveData() {
        sharedPreferences.edit().apply {
            putString("fall_items", gson.toJson(_fallItems.toList()))
            putString("winter_items", gson.toJson(_winterItems.toList()))
            apply()
        }
    }

    private fun getDefaultFallItems() = listOf(
        ChecklistItem(1, "Visa", false, "This Week"),
        ChecklistItem(2, "Flight", false, "This Week"),
        ChecklistItem(3, "Accommodation", false, "This Week"),
        ChecklistItem(4, "School Enrollment", false, "This Week"),
        ChecklistItem(5, "Learn Route to School", false, "This Week"),
        ChecklistItem(6, "Pay Rent", false, "This Week"),
        ChecklistItem(7, "Plan Travel", false, "Upcoming"),
        ChecklistItem(8, "Get Metro Card", false, "Upcoming")
    )

    private fun getDefaultWinterItems() = listOf(
        ChecklistItem(9, "Winter Visa Extension", false, "This Week"),
        ChecklistItem(10, "Warm Clothing Shopping", false, "This Week"),
        ChecklistItem(11, "Winter Housing Check", false, "This Week"),
        ChecklistItem(12, "Health Insurance Renewal", false, "Upcoming"),
        ChecklistItem(13, "Holiday Travel Planning", false, "Upcoming")
    )

    fun loadFallTerm() {
        currentTerm = "Fall Term"
    }

    fun loadWinterTerm() {
        currentTerm = "Winter Term"
    }

    fun toggleItem(id: Int) {
        val targetList = if (currentTerm == "Fall Term") _fallItems else _winterItems
        val index = targetList.indexOfFirst { it.id == id }
        if (index != -1) {
            targetList[index] = targetList[index].copy(isDone = !targetList[index].isDone)
            saveData()
        }
    }

    fun addItem(title: String, category: String) {
        val targetList = if (currentTerm == "Fall Term") _fallItems else _winterItems
        val newId = ((_fallItems.map { it.id } + _winterItems.map { it.id }).maxOfOrNull { it } ?: 0) + 1
        targetList.add(ChecklistItem(newId, title, false, category))
        saveData()
    }

    fun getProgress(): Float {
        val currentItems = items
        if (currentItems.isEmpty()) return 0f
        val completed = currentItems.count { it.isDone }
        return completed.toFloat() / currentItems.size
    }
}
