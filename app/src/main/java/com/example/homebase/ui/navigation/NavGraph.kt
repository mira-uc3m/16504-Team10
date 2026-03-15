package com.example.homebase.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.homebase.data.view.ScheduleViewModel
import com.example.homebase.ui.screens.AddScheduleScreen
import com.example.homebase.ui.screens.CurrencyScreen
import com.example.homebase.ui.screens.HomeScreen
import com.example.homebase.ui.screens.ScheduleScreen
import com.example.homebase.ui.screens.SettingsScreen
import com.example.homebase.ui.screens.ScheduleScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    val scheduleViewModel: ScheduleViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

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
            ScheduleScreen(
                navController = navController,
                viewModel = scheduleViewModel
            )
        }
        composable(route = Screen.AddSchedule.route) {
            AddScheduleScreen(
                onBack = { navController.popBackStack() },
                scheduleViewModel = scheduleViewModel
            )
        }
    }
}