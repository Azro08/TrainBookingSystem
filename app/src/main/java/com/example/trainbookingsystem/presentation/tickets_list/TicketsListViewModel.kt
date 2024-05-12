package com.example.trainbookingsystem.presentation.tickets_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainbookingsystem.data.model.Ticket
import com.example.trainbookingsystem.data.model.getTripDuration
import com.example.trainbookingsystem.data.repository.TicketsRepository
import com.example.trainbookingsystem.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketsListViewModel @Inject constructor(
    private val ticketsRepository: TicketsRepository
) : ViewModel() {

    private val _ticketsList = MutableStateFlow<ScreenState<List<Ticket>?>>(ScreenState.Loading())
    val ticketsList = _ticketsList

    fun getTickets(
        startDestination: String,
        endDestination: String,
        departureTime: String,
        arrivalTime: String
    ) = viewModelScope.launch {
        ticketsRepository.getFilteredTickets(
            startDestination,
            endDestination,
            departureTime,
            arrivalTime
        ).let {
            if (it.isNotEmpty()) _ticketsList.value = ScreenState.Success(it)
            else _ticketsList.value = ScreenState.Error("Нет доступных билетов")
        }
    }

    fun getFilteredTickets(
        minPrice: Double,
        maxPrice: Double?,
        minDuration: Int,
        maxDuration: Int?
    ): List<Ticket> {
        val allTickets = _ticketsList.value.data ?: return emptyList()

        return allTickets.filter { ticket ->
            Log.d("getFilteredTickets", "from: ${ticket.startDestination} to: ${ticket.endDestination} duration: ${ticket.getTripDuration()}, minDuration: $minDuration, maxDuration: $maxDuration")

            val priceInRange = (ticket.price[0] >= minPrice) &&
                    (maxPrice == null || ticket.price[0] <= maxPrice)
            val durationInRange =
                (ticket.getTripDuration() >= minDuration) &&
                        (maxDuration == null || ticket.getTripDuration() <= maxDuration)
            priceInRange && durationInRange
        }
    }


    fun deleteTicket(ticketId: String) = viewModelScope.launch {
        ticketsRepository.deleteTicket(ticketId)
        getAllTickets()
    }

    fun getAllTickets() = viewModelScope.launch {
        ticketsRepository.getAllTickets().let {
            if (it.isNotEmpty()) _ticketsList.value = ScreenState.Success(it)
            else _ticketsList.value = ScreenState.Error("Нет доступных билетов")
        }
    }

    fun getAllActiveTickets() = viewModelScope.launch {
        ticketsRepository.getAllActiveTickets().let {
            if (it.isNotEmpty()) _ticketsList.value = ScreenState.Success(it)
            else _ticketsList.value = ScreenState.Error("Нет доступных билетов")
        }
    }

}