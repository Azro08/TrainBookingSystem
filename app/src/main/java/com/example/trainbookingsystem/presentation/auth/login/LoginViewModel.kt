package com.example.trainbookingsystem.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainbookingsystem.data.repository.AuthRepository
import com.example.trainbookingsystem.util.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRep: AuthRepository,
) : ViewModel() {

    private val _loginState = MutableStateFlow<ScreenState<String>>(ScreenState.Loading())
    val loginState = _loginState

    fun login(email: String, password: String, coroutineScope: CoroutineScope) = viewModelScope.launch {
        try {
            authRep.login(email, password, coroutineScope).let {
                if (it == "Done") authRep.getUserRole().let { role ->
                    if (!role.isNullOrEmpty()) _loginState.value = ScreenState.Success(role)
                    else _loginState.value = ScreenState.Error(it)
                }
                else _loginState.value = ScreenState.Error(it)
            }
        } catch (e: Exception) {
            e.message.toString()
        }
    }

}