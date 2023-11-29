package com.example.trainbookingsystem.presentation.admin.tickets_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainbookingsystem.data.model.Ticket
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

    fun getTickets(startDestination: String, endDestination: String, departureTime : String) = viewModelScope.launch {
        ticketsRepository.getTickets(startDestination, endDestination, departureTime).let {
            if (it.isNotEmpty()) _ticketsList.value = ScreenState.Success(it)
            else _ticketsList.value = ScreenState.Error("Нет доступных билетов")
        }
    }

    fun getAllTickets() = viewModelScope.launch {
        ticketsRepository.getAllTickets().let {
            if (it.isNotEmpty()) _ticketsList.value = ScreenState.Success(it)
            else _ticketsList.value = ScreenState.Error("Нет доступных билетов")
        }
    }

}