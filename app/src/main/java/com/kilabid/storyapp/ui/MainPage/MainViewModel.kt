package com.kilabid.storyapp.ui.MainPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kilabid.storyapp.data.local.UserModel
import com.kilabid.storyapp.data.remote.response.ListStoryItem
import com.kilabid.storyapp.data.repository.UserRepository
import com.kilabid.storyapp.di.ResultState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun listStory() {
        viewModelScope.launch {
            when (val result = repository.getStories().first()) {
                is ResultState.Success -> {
                    _listStory.value = result.data.listStory
                }

                else -> {

                }
            }
        }
    }
}