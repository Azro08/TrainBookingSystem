package com.example.trainbookingsystem.data.repository

import android.util.Log
import com.example.trainbookingsystem.data.model.Ticket
import com.google.api.Billing.BillingDestination
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TicketsRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val ticketsCollection = firestore.collection("tickets")


    suspend fun getAllTickets(): List<Ticket> {
        return ticketsCollection.get().await().toObjects(Ticket::class.java)
    }

    suspend fun getTickets(startDestination: String, endDestination: String, departureTime : String) : List<Ticket>{
        return try {
            val querySnapshot = ticketsCollection
                .whereEqualTo("startDestination", startDestination)
                .whereEqualTo("endDestination", endDestination)
                .whereEqualTo("departureTime", departureTime)
                .get().await()

            val ticketsList = querySnapshot.toObjects(Ticket::class.java)
            Log.d("TicketQuery", "Number of tickets retrieved: ${ticketsList.size}")

            return ticketsList
        } catch (e: Exception) {
            Log.d("TicketError", e.message.toString())
            emptyList()
        }
    }

    suspend fun getTicketById(id: String): Ticket? {
        return try {
            ticketsCollection.document(id).get().await().toObject(Ticket::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addTicket(ticket: Ticket): String {
        return try {
            ticketsCollection.document(ticket.id).set(ticket).await()
            "Done"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

    suspend fun deleteTicket(ticketId: String): String {
        return try {
            ticketsCollection.document(ticketId).delete().await()
            "Done"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

}