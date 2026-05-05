package com.example.homebase.data.view

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun signUp(email: String, pass: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Registration failed") }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Login failed") }
    }

    fun logout(onSuccess: () -> Unit) {
        auth.signOut()
        onSuccess()
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid
}