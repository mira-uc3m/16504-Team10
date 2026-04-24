package com.example.homebase.data.view

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ChecklistItem(
    val id: Int,
    val title: String,
    val isDone: Boolean = false,
    val category: String = "This Week",
    val subItems: List<ChecklistItem> = emptyList(),
    val hasDetailArrow: Boolean = false
)

class ChecklistViewModel(application: Application) : AndroidViewModel(application) {
    private val _items = mutableStateListOf<ChecklistItem>()
    val items: List<ChecklistItem> get() = _items

    private val sharedPreferences = application.getSharedPreferences("checklist_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    init {
        loadSavedData()
    }

    private fun loadSavedData() {
        val json = sharedPreferences.getString("checklist_items", null)
        if (json != null) {
            val type = object : TypeToken<List<ChecklistItem>>() {}.type
            val savedItems: List<ChecklistItem> = gson.fromJson(json, type)
            _items.clear()
            _items.addAll(savedItems)
        } else {
            loadFallTerm() // Default data if nothing saved
        }
    }

    private fun saveData() {
        val json = gson.toJson(_items.toList())
        sharedPreferences.edit().putString("checklist_items", json).apply()
    }

    fun loadFallTerm() {
        _items.clear()
        _items.addAll(listOf(
            ChecklistItem(1, "Visa", false, "This Week", subItems = listOf(
                ChecklistItem(101, "Apostille Documents", false),
                ChecklistItem(102, "Medical Certificate", false)
            )),
            ChecklistItem(2, "School", false, "This Week", subItems = listOf(
                ChecklistItem(201, "Orientation Sign Up", false),
                ChecklistItem(202, "Enroll in Classes", false)
            )),
            ChecklistItem(3, "Book Flights", false, "This Week", hasDetailArrow = true),
            ChecklistItem(4, "Accommodation", false, "Upcoming", subItems = listOf(
                ChecklistItem(301, "Pay Rent", false),
                ChecklistItem(302, "Key Pickup", false)
            )),
            ChecklistItem(5, "Activate Insurance", false, "Upcoming", hasDetailArrow = true),
            ChecklistItem(6, "Get Metro Card", false, "Upcoming")
        ))
        saveData()
    }

    fun loadWinterTerm() {
        _items.clear()
        _items.addAll(listOf(
            ChecklistItem(7, "Winter Visa Extension", false, "This Week"),
            ChecklistItem(8, "Warm Clothing Shopping", false, "This Week"),
            ChecklistItem(9, "Health Insurance Renewal", false, "Upcoming")
        ))
        saveData()
    }

    fun toggleItem(id: Int, parentId: Int? = null) {
        if (parentId == null) {
            val index = _items.indexOfFirst { it.id == id }
            if (index != -1) {
                _items[index] = _items[index].copy(isDone = !_items[index].isDone)
            }
        } else {
            val parentIndex = _items.indexOfFirst { it.id == parentId }
            if (parentIndex != -1) {
                val parent = _items[parentIndex]
                val updatedSubItems = parent.subItems.map { 
                    if (it.id == id) it.copy(isDone = !it.isDone) else it 
                }
                _items[parentIndex] = parent.copy(subItems = updatedSubItems)
            }
        }
        saveData()
    }

    fun addItem(title: String, category: String) {
        val newId = (System.currentTimeMillis() % 10000).toInt()
        _items.add(ChecklistItem(newId, title, false, category))
        saveData()
    }

    fun getProgress(): Float {
        val allItems = _items.flatMap { listOf(it) + it.subItems }
        if (allItems.isEmpty()) return 0f
        return allItems.count { it.isDone }.toFloat() / allItems.size
    }
}
