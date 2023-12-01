package com.example.trainbookingsystem.data.repository

import android.util.Log
import com.example.trainbookingsystem.data.model.TicketCheck
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TicketsHistoryRepository @Inject constructor(
    firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    private val checksCollection = firestore.collection("checks")

    suspend fun getUsersTicketsHistory(): List<TicketCheck> {
        return try {
            val userId = firebaseAuth.currentUser?.uid ?: ""
            val checks = checksCollection.whereEqualTo("userId", userId).get().await()
            checks.toObjects(TicketCheck::class.java)
        } catch (e: FirebaseFirestoreException) {
            Log.d("TicketsHistoryRepository", e.message.toString())
            emptyList()
        }
    }

    suspend fun getAlTicketsHistory(): List<TicketCheck> {
        return try {
            val checks = checksCollection.get().await()
            checks.toObjects(TicketCheck::class.java)
        } catch (e: FirebaseFirestoreException) {
            Log.d("TicketsHistoryRepository", e.message.toString())
            emptyList()
        }
    }

}