package com.kilabid.storyapp.ui.RegisterPage

import androidx.lifecycle.ViewModel
import com.kilabid.storyapp.data.remote.response.RegisterResponse
import com.kilabid.storyapp.data.repository.UserRepository
import com.kilabid.storyapp.di.ResultState
import kotlinx.coroutines.flow.Flow

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {
    fun userRegister(
        name: String,
        email: String,
        password: String,
    ): Flow<ResultState<RegisterResponse>> {
        return repository.userRegister(name, email, password)
    }


}