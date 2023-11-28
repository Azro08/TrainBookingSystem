package com.example.trainbookingsystem.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainbookingsystem.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRep: AuthRepository,
) : ViewModel() {

    private val _loginState = MutableStateFlow("")
    val loginState = _loginState

    fun login(email: String, password: String) = viewModelScope.launch {
        try {
            authRep.login(email, password).let {
                if (it == "Done") authRep.getUserRole().let { role ->
                    if (!role.isNullOrEmpty()) _loginState.value = role
                    else _loginState.value = "User role can't be found!"
                }
                else _loginState.value = it
            }
        } catch (e: Exception) {
            e.message.toString()
        }
    }

}