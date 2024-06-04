package com.kilabid.storyapp.ui.LoginPage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kilabid.storyapp.data.local.UserModel
import com.kilabid.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }

    fun userLogin(email: String, password: String) = repository.userLogin(email, password)
}