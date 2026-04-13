package com.example.homebase.data.repository

import com.example.homebase.data.model.ScheduleEvent
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ScheduleRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("scheduleEvents")

    suspend fun postEvent(event: ScheduleEvent): Boolean {
        return try {
            collection.document(event.id).set(event).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getEvents(userId: String): List<ScheduleEvent> {
        return try {
            val querySnapshot = collection
                .whereEqualTo("userId", userId)
                .get()
                .await()
            querySnapshot.toObjects(ScheduleEvent::class.java)
        } catch (e: Exception) {
            // THIS LINE IS THE TEST:
            println("FIREBASE_ERROR: ${e.message}")
            emptyList()
        }
    }

    suspend fun deleteEvent(eventId: String): Boolean {
        return try {
            collection.document(eventId).delete().await()
            true
        } catch (e: Exception) {
            println("FIREBASE_ERROR: ${e.message}")
            false
        }
    }
}