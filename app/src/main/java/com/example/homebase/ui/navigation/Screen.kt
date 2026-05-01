package com.example.homebase.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Schedule : Screen("schedule", "My Schedule", Icons.Default.DateRange)
    object AddSchedule : Screen("add_schedule", "Add to Schedule", Icons.Default.Add)
    object Notifications : Screen("notifications", "Notifications", Icons.Default.Notifications)
    object QuickLinks : Screen("quick_links", "Quick Links", Icons.Default.Link)
    object Checklist : Screen("checklist_screen", "Checklist", Icons.Default.FormatListBulleted)
    object ClassList : Screen("classlist_screen", "Class List", Icons.Default.ListAlt)
}
