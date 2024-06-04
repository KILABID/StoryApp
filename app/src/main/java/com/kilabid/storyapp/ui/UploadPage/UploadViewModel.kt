package com.kilabid.storyapp.ui.UploadPage

import androidx.lifecycle.ViewModel
import com.kilabid.storyapp.data.repository.UserRepository
import java.io.File

class UploadViewModel(private val repository: UserRepository) : ViewModel() {
    fun uploadImg(file: File, description: String) = repository.uploadImg(file, description)
}