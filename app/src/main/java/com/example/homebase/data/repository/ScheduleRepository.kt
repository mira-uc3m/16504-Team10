package com.example.homebase.data.repository

import com.example.homebase.data.model.ScheduleEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ScheduleRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("scheduleEvents")

    // POST: Send a single event to Firebase
    suspend fun postEvent(event: ScheduleEvent): Boolean {
        return try {
            collection.document(event.id).set(event).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // GET: Fetch all events from Firebase
    suspend fun getEvents(): List<ScheduleEvent> {
        return try {
            val querySnapshot = collection.get().await()
            querySnapshot.toObjects(ScheduleEvent::class.java)
        } catch (e: Exception) {
            // THIS LINE IS THE TEST:
            println("FIREBASE_ERROR: ${e.message}")
            emptyList()
        }
    }
}