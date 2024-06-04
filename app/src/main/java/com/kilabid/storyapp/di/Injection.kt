package com.kilabid.storyapp.di

import android.content.Context
import com.kilabid.storyapp.data.local.UserPreference
import com.kilabid.storyapp.data.local.dataStore
import com.kilabid.storyapp.data.remote.api.ApiConfig
import com.kilabid.storyapp.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(pref, apiService)
    }
}