package com.example.trainbookingsystem.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainbookingsystem.data.model.TicketCheck
import com.example.trainbookingsystem.data.repository.TicketsHistoryRepository
import com.example.trainbookingsystem.util.Constants
import com.example.trainbookingsystem.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val checkHistoryRepository: TicketsHistoryRepository
) : ViewModel() {

    private val _history = MutableStateFlow<ScreenState<List<TicketCheck>?>>(ScreenState.Loading())
    val history = _history

    fun refresh(userRole: String) {
        if (userRole == Constants.USER) getUsersHistory()
        else getAllHistory()
    }

    fun getAllHistory() = viewModelScope.launch {
        checkHistoryRepository.getAlTicketsHistory().let {
            if (it.isNotEmpty()) _history.value = ScreenState.Success(it)
            else _history.value = ScreenState.Error("Нет заказов")
        }
    }

    fun getUsersHistory() = viewModelScope.launch {
        checkHistoryRepository.getUsersTicketsHistory().let {
            if (it.isNotEmpty()) _history.value = ScreenState.Success(it)
            else _history.value = ScreenState.Error("Нет заказов")
        }
    }

}