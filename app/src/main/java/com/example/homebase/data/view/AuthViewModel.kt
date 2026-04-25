package com.example.homebase.data.view

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun signUp(email: String, pass: String, onResult: (String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { 
                onResult(null) 
            }
            .addOnFailureListener { exception ->
                onResult(exception.localizedMessage ?: "Sign up failed")
            }
    }

    fun login(email: String, pass: String, onResult: (String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { 
                onResult(null) 
            }
            .addOnFailureListener { exception ->
                onResult(exception.localizedMessage ?: "Login failed")
            }
    }

    fun logout(onSuccess: () -> Unit) {
        auth.signOut()
        onSuccess()
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid
}
