package com.example.homebase.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.homebase.ui.screens.HomeScreen
import com.example.homebase.ui.screens.CurrencyScreen
import com.example.homebase.ui.screens.SettingsScreen
import com.example.homebase.ui.screens.ScheduleScreen
import com.example.homebase.ui.screens.MapScreen // 1. Add this import

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController)
        }
        composable(route = "currency_screen") {
            CurrencyScreen()
        }
        composable(route = "schedule_screen") {
            ScheduleScreen()
        }
        composable(route = "map") {
            MapScreen(navController)
        }
    }
}