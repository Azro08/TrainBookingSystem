package com.example.trainbookingsystem.presentation.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainbookingsystem.data.model.Account
import com.example.trainbookingsystem.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRep: AuthRepository,
) : ViewModel() {

    private val _registerState = MutableStateFlow("")
    val registerState = _registerState


    fun register(account: Account, password: String) = viewModelScope.launch {
        try {
            authRep.signup(account.email, password).let { uid->
                account.id = uid
                if (uid.isNotEmpty()) {
                    authRep.saveUser(account).let{
                        _registerState.value = it
                    }
                } else {
                    _registerState.value = "Error creating account"
                }
            }

        } catch (e : Exception){
            e.message.toString()
        }
    }

}