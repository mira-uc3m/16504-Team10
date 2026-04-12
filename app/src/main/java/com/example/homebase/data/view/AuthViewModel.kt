package com.example.homebase.data.view

import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    private val auth = com.google.firebase.auth.FirebaseAuth.getInstance()

    fun signUp(email: String, pass: String, onSuccess: () -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onSuccess() }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onSuccess() }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid
}