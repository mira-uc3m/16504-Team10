package com.example.homebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.homebase.ui.navigation.AppNavGraph
import com.example.homebase.ui.theme.HomeBaseTheme
import com.example.homebase.ui.screens.LoginScreen
import com.example.homebase.data.view.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HomeBaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Initialize your ViewModel and State
                    val authViewModel: AuthViewModel = viewModel()
                    val navController = rememberNavController()

                    // 2. Track the user state (using mutableStateOf so Compose updates)
                    var user by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

                    // 3. Conditional Logic for which screen to show
                    if (user == null) {
                        LoginScreen(authViewModel = authViewModel) {
                            // On Success, update the state to trigger a recomposition
                            user = FirebaseAuth.getInstance().currentUser
                        }
                    } else {
                        // User is logged in, show the actual app
                        AppNavGraph(navController = navController)
                    }
                }
            }
        }
    }
}