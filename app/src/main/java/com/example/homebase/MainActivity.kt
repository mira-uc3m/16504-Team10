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
                    val authViewModel: AuthViewModel = viewModel()

                    // Listen to Firebase Auth state changes
                    var user by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

                    DisposableEffect(Unit) {
                        val listener = FirebaseAuth.AuthStateListener { auth ->
                            user = auth.currentUser
                        }
                        FirebaseAuth.getInstance().addAuthStateListener(listener)
                        onDispose {
                            FirebaseAuth.getInstance().removeAuthStateListener(listener)
                        }
                    }

                    // Conditional Logic for which screen to show
                    if (user == null) {
                        LoginScreen(authViewModel = authViewModel) {
                            // The listener above will handle updating the 'user' state
                        }
                    } else {
                        // User is logged in, show the actual app
                        // Use user.uid as a key to ensure we reset navigation and state for every new session
                        key(user?.uid) {
                            val navController = rememberNavController()
                            AppNavGraph(navController = navController)
                        }
                    }
                }
            }
        }
    }
}