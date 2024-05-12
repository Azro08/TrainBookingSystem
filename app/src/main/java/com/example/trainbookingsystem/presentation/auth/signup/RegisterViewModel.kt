package com.example.trainbookingsystem.presentation.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainbookingsystem.data.model.Account
import com.example.trainbookingsystem.data.repository.AuthRepository
import com.example.trainbookingsystem.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRep: AuthRepository,
) : ViewModel() {

    private val _registerState = MutableStateFlow<ScreenState<String>>(ScreenState.Loading())
    val registerState = _registerState


    fun register(account: Account) = viewModelScope.launch {
        try {
            authRep.saveUser(account).let {
                if (it == "Done") _registerState.value = ScreenState.Success(it)
                else _registerState.value = ScreenState.Error(it)
            }

        } catch (e: Exception) {
            e.message.toString()
        }
    }

}