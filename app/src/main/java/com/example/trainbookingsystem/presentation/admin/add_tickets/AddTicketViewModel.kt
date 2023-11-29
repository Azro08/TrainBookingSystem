package com.example.trainbookingsystem.presentation.admin.add_tickets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainbookingsystem.data.model.Ticket
import com.example.trainbookingsystem.data.repository.TicketsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTicketViewModel @Inject constructor(
    private val ticketsRepository: TicketsRepository
) : ViewModel() {

    private val _ticketSavedState = MutableStateFlow("")
    val ticketSavedState = _ticketSavedState

    fun saveTicket(ticket: Ticket) = viewModelScope.launch {
        ticketsRepository.addTicket(ticket).let {
            _ticketSavedState.value = it
        }
    }

}