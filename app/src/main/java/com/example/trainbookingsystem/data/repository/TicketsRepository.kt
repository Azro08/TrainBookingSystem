package com.example.trainbookingsystem.data.repository

import android.util.Log
import com.example.trainbookingsystem.data.model.Ticket
import com.example.trainbookingsystem.data.model.TicketCheck
import com.example.trainbookingsystem.util.DateUtils
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class TicketsRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val ticketsCollection = firestore.collection("tickets")


    suspend fun getAllTickets(): List<Ticket> {
        return ticketsCollection.get().await().toObjects(Ticket::class.java)
    }

    suspend fun getAllActiveTickets(): List<Ticket> {
        return ticketsCollection.get().await().toObjects(Ticket::class.java)
            .filter { !isDepartureTimePassed(it.departureTime) }
    }


    suspend fun getFilteredTickets(
        startDestination: String,
        endDestination: String,
        departureTime: String,
        returnTime: String
    ): List<Ticket> {
        return try {
            val ticketsList = getAllActiveTickets()

            var filteredList = ticketsList

            if (startDestination.isNotEmpty() && endDestination.isEmpty() && departureTime.isEmpty() && returnTime.isEmpty()) {
                filteredList = ticketsList.filter { it.startDestination == startDestination }
            }
            if (endDestination.isNotEmpty() && startDestination.isEmpty() && departureTime.isEmpty() && returnTime.isEmpty()) {
                filteredList = ticketsList.filter { it.endDestination == endDestination }
            }
            if (startDestination.isNotEmpty() && endDestination.isNotEmpty() && departureTime.isEmpty() && returnTime.isEmpty()) {
                filteredList = ticketsList.filter { it.startDestination == startDestination && it.endDestination == endDestination }
            }

            if (departureTime.isNotEmpty() && returnTime.isNotEmpty() && startDestination.isEmpty() && endDestination.isEmpty()) {
                // Filter based on departure and return time
                filteredList = ticketsList.filter {
                    isTimeInRange(it.departureTime, departureTime, returnTime) ||
                            isTimeInRange(it.departureTime, departureTime, returnTime)
                }
            }

            if (departureTime.isNotEmpty() && returnTime.isNotEmpty() && startDestination.isNotEmpty() && endDestination.isEmpty()) {
                // Filter based on start destination, departure, and return time
                filteredList = ticketsList.filter {
                    (it.startDestination == startDestination && isTimeInRange(it.departureTime, departureTime, returnTime)) ||
                            (it.endDestination == startDestination && isTimeInRange(it.departureTime, departureTime, returnTime))
                }
            }

            if (departureTime.isNotEmpty() && returnTime.isNotEmpty() && startDestination.isEmpty() && endDestination.isNotEmpty()) {
                // Filter based on end destination, departure, and return time
                filteredList = ticketsList.filter {
                    (it.endDestination == endDestination && isTimeInRange(it.departureTime, departureTime, returnTime)) ||
                            (it.startDestination == endDestination && isTimeInRange(it.departureTime, departureTime, returnTime))
                }
            }

            if (departureTime.isNotEmpty() && returnTime.isNotEmpty() && startDestination.isNotEmpty() && endDestination.isNotEmpty()) {
                // Filter based on start and end destination, departure, and return time
                filteredList = ticketsList.filter {
                    ((it.startDestination == startDestination && it.endDestination == endDestination) &&
                            (isTimeInRange(it.departureTime, departureTime, returnTime) || isTimeInRange(it.departureTime, departureTime, returnTime))) ||
                            ((it.startDestination == endDestination && it.endDestination == startDestination) &&
                                    (isTimeInRange(it.departureTime, departureTime, returnTime) || isTimeInRange(it.departureTime, departureTime, returnTime)))
                }
            }

            if (filteredList.isEmpty()) {
                if (startDestination.isNotEmpty() && endDestination.isNotEmpty()) {
                    val connectingFlights = ticketsList.filter { it.startDestination == startDestination && it.endDestination != endDestination }
                    connectingFlights.forEach { connectingFlight ->
                        val intermediateDestinations = ticketsList.filter { it.startDestination == connectingFlight.endDestination && it.endDestination == endDestination }
                        if (intermediateDestinations.isNotEmpty()) {
                            val validIntermediateDestinations = intermediateDestinations.filter { intermediateFlight ->
                                val connectingFlightArrivalTime = DateUtils.parseDateTime(connectingFlight.arrivalTime)
                                val intermediateFlightDepartureTime = DateUtils.parseDateTime(intermediateFlight.departureTime)
                                val timeDifferenceHours = DateUtils.calculateTimeDifferenceHours(connectingFlightArrivalTime, intermediateFlightDepartureTime)
                                Log.d("TimeDifference", "First ${connectingFlight.arrivalTime},,,,, second : ${intermediateFlight.departureTime}")
                                timeDifferenceHours in 1..12
                            }
                            if (validIntermediateDestinations.isNotEmpty()) {
                                filteredList = filteredList + connectingFlight + validIntermediateDestinations
                            }
                        }
                    }
                }
            }

            filteredList
        } catch (e: Exception) {
            Log.d("FilteredTicketError", e.message.toString())
            emptyList()
        }
    }

    private fun isDepartureTimePassed(departureTime: String): Boolean {
        val currentTime = Date()
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val departureDateTime = dateFormatter.parse(departureTime) ?: return false
        return currentTime.after(departureDateTime)
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