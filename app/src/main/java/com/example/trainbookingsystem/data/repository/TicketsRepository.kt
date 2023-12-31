package com.example.trainbookingsystem.data.repository

import android.util.Log
import com.example.trainbookingsystem.data.model.Ticket
import com.example.trainbookingsystem.data.model.TicketCheck
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class TicketsRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val ticketsCollection = firestore.collection("tickets")


    suspend fun getAllTickets(): List<Ticket> {
        return ticketsCollection.get().await().toObjects(Ticket::class.java)
    }


    suspend fun getFilteredTickets(
        startDestination: String,
        endDestination: String,
        departureTime: String,
        arrivalTime: String
    ): List<Ticket> {
        return try {
            val ticketsList = getAllTickets()
            var filteredList = ticketsList
            if (startDestination.isNotEmpty() && endDestination.isEmpty() && departureTime.isEmpty() && arrivalTime.isEmpty()) {
               filteredList = ticketsList.filter { it.startDestination == startDestination }
                Log.d("FilteredTicket", filteredList.toString())
            }
            if (endDestination.isNotEmpty() && startDestination.isEmpty() && departureTime.isEmpty() && arrivalTime.isEmpty()) {
                filteredList =ticketsList.filter { it.endDestination == endDestination }
                Log.d("FilteredTicket", filteredList.toString())
            }
            if (startDestination.isNotEmpty() && endDestination.isNotEmpty() && departureTime.isEmpty() && arrivalTime.isEmpty()) {
                filteredList =ticketsList.filter { it.startDestination == startDestination && it.endDestination == endDestination }
                Log.d("FilteredTicket", filteredList.toString())
            }

            if (departureTime.isNotEmpty() && arrivalTime.isNotEmpty() && startDestination.isEmpty() && endDestination.isEmpty()) {
                filteredList =ticketsList.filter {
                    isTimeInRange(it.departureTime, departureTime, arrivalTime)
                }
                Log.d("FilteredTicket", filteredList.toString())
            }

            if (departureTime.isNotEmpty() && arrivalTime.isNotEmpty() && startDestination.isNotEmpty() && endDestination.isEmpty()) {
                filteredList =ticketsList.filter {
                    it.startDestination == startDestination && isTimeInRange(
                        it.departureTime,
                        departureTime,
                        arrivalTime
                    )
                }
                Log.d("FilteredTicket", filteredList.toString())
            }
            if (departureTime.isNotEmpty() && arrivalTime.isNotEmpty() && startDestination.isEmpty() && endDestination.isNotEmpty()) {
                filteredList =ticketsList.filter {
                    it.endDestination == endDestination && isTimeInRange(
                        it.departureTime,
                        departureTime,
                        arrivalTime
                    )
                }
                Log.d("FilteredTicket", filteredList.toString())
            }
            if (departureTime.isNotEmpty() && arrivalTime.isNotEmpty() && startDestination.isNotEmpty() && endDestination.isNotEmpty()) {
                filteredList =ticketsList.filter {
                    it.startDestination == startDestination && it.endDestination == endDestination && isTimeInRange(
                        it.departureTime,
                        departureTime,
                        arrivalTime
                    )
                }
                Log.d("FilteredTicket", filteredList.toString())
            }

            filteredList
        } catch (e: Exception) {
            Log.d("FilteredTicketError", e.message.toString())
            emptyList()
        }
    }


    private fun isTimeInRange(
        ticketDepartureTime: String,
        departureTime: String?,
        arrivalTime: String?
    ): Boolean {
        if (departureTime.isNullOrEmpty() || arrivalTime.isNullOrEmpty()) {
            return true // If departureTime or arrivalTime is empty or null, consider it within range
        }

        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

        try {
            val parsedTicketDepartureTime = dateFormatter.parse(ticketDepartureTime) ?: return false
            val parsedDepartureTime = dateFormatter.parse(departureTime) ?: return false
            val parsedArrivalTime = dateFormatter.parse(arrivalTime) ?: return false

            return parsedTicketDepartureTime in parsedDepartureTime..parsedArrivalTime
        } catch (e: ParseException) {
            return false
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

    suspend fun buyTicket(check: TicketCheck): String {
        return try {
            val checksCollection = firestore.collection("checks")
            checksCollection.document(check.id).set(check).await()
            val freeSeats: MutableList<Int> =
                (getTicketById(check.ticket.id)?.freeSeats ?: emptyList()).toMutableList()
            freeSeats.remove(check.seatNumber)
            ticketsCollection.document(check.ticket.id).update("freeSeats", freeSeats)
            "Done"
        } catch (e: Exception) {
            e.message.toString()
        }
    }

}