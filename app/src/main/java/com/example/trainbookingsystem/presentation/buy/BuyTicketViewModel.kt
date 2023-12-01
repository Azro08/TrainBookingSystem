package com.example.trainbookingsystem.presentation.buy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainbookingsystem.data.model.Ticket
import com.example.trainbookingsystem.data.model.TicketCheck
import com.example.trainbookingsystem.data.repository.TicketsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyTicketViewModel @Inject constructor(
    private val ticketsRepository: TicketsRepository
) : ViewModel(){

    private val _buyTicketState = MutableStateFlow("")
    val buyTicketState = _buyTicketState

    private val _ticket = MutableStateFlow<Ticket?>(null)
    val ticket = _ticket

    fun getTicket(ticketId : String) = viewModelScope.launch {
        ticketsRepository.getTicketById(ticketId).let {
            if (it != null) _ticket.value = it
            else _ticket.value = null
        }
    }

    fun buyTicket(ticketCheck : TicketCheck) = viewModelScope.launch {
        ticketsRepository.buyTicket(ticketCheck).let {
            _buyTicketState.value = it
        }
    }

}