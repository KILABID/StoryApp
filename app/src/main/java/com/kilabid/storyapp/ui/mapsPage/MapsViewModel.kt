package com.kilabid.storyapp.ui.mapsPage

import androidx.lifecycle.ViewModel
import com.kilabid.storyapp.data.repository.UserRepository

class MapsViewModel(private val repository: UserRepository): ViewModel() {
    fun getLocation() = repository.getLocation()
}