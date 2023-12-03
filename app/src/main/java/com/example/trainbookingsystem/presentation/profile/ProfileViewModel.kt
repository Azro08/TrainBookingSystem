package com.example.trainbookingsystem.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trainbookingsystem.data.model.Account
import com.example.trainbookingsystem.data.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val _user = MutableStateFlow<Account?>(null)
    val user = _user

    private val _isUpdated = MutableStateFlow("")
    val isUpdated = _isUpdated

    init {
        getUser()
    }

    private fun getUser() = viewModelScope.launch {
        usersRepository.getAccount().let {
            if (it != null) _user.value = it
            else _user.value = null
        }
    }

    fun updateUserFields(
        updatedFields: Map<String, Any>,
        password: String,
        oldPassword: String = "",
    ) = viewModelScope.launch {
        usersRepository.updateAccount(updatedFields, password, oldPassword).let {
            _isUpdated.value = it
        }
    }

}