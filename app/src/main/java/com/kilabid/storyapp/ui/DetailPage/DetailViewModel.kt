package com.kilabid.storyapp.ui.DetailPage

import androidx.lifecycle.ViewModel
import com.kilabid.storyapp.data.repository.UserRepository

class DetailViewModel(private val repository: UserRepository) : ViewModel() {
    suspend fun getDetailStories(id: String) = repository.getDetailStories(id)

}