package com.kilabid.storyapp.ui.MainPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kilabid.storyapp.data.local.UserModel
import com.kilabid.storyapp.data.remote.response.ListStoryItem
import com.kilabid.storyapp.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
    val story: LiveData<PagingData<ListStoryItem>> = repository.getStories().cachedIn(viewModelScope)


}