package com.example.homebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.homebase.ui.navigation.AppNavGraph
import com.example.homebase.ui.navigation.Screen
import com.example.homebase.ui.theme.HomeBaseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeBaseTheme {
                val navController = rememberNavController()
                
                Scaffold(
                    containerColor = Color.White,
                    bottomBar = {
                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                        val currentDestination = navBackStackEntry?.destination
                        
                        // Show bottom bar only on Home and Settings screens
                        val showBottomBar = currentDestination?.route in listOf(Screen.Home.route, Screen.Settings.route)
                        
                        if (showBottomBar) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp),
                                color = Color.White,
                                tonalElevation = 0.dp
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val navItems = listOf(Screen.Home, Screen.Settings)
                                    navItems.forEach { screen ->
                                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                                        
                                        Box(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .clickable(
                                                    interactionSource = null,
                                                    indication = null
                                                ) {
                                                    navController.navigate(screen.route) {
                                                        popUpTo(navController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                // Selected Pill Style
                                                Surface(
                                                    color = Color(0xFF3022A6),
                                                    shape = RoundedCornerShape(24.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Icon(
                                                            imageVector = screen.icon,
                                                            contentDescription = null,
                                                            tint = Color.White,
                                                            modifier = Modifier.size(20.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Text(
                                                            text = screen.title,
                                                            color = Color.White,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 14.sp
                                                        )
                                                    }
                                                }
                                            } else {
                                                // Unselected Icon Style
                                                Icon(
                                                    imageVector = screen.icon,
                                                    contentDescription = null,
                                                    tint = Color.Gray,
                                                    modifier = Modifier.size(28.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = innerPadding.calculateBottomPadding()),
                        color = Color.White
                    ) {
                        AppNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
