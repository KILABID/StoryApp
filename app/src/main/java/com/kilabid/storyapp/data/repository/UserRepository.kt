package com.kilabid.storyapp.data.repository

import com.google.gson.Gson
import com.kilabid.storyapp.data.local.UserModel
import com.kilabid.storyapp.data.local.UserPreference
import com.kilabid.storyapp.data.remote.api.ApiService
import com.kilabid.storyapp.data.remote.response.DetailStoryResponse
import com.kilabid.storyapp.data.remote.response.ListStoryResponse
import com.kilabid.storyapp.data.remote.response.LoginResponse
import com.kilabid.storyapp.data.remote.response.RegisterResponse
import com.kilabid.storyapp.data.remote.response.UploadResponse
import com.kilabid.storyapp.di.ResultState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UserRepository private constructor(
    private var userPreference: UserPreference,
    private var apiService: ApiService,
) {
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun userLogin(email: String, password: String): Flow<ResultState<LoginResponse>> = flow {
        emit(ResultState.Loading)
        try {
            val response = apiService.login(email, password)
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ResultState.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun userRegister(
        name: String,
        email: String,
        password: String,
    ): Flow<ResultState<RegisterResponse>> = flow {
        try {
            val response = apiService.register(name, email, password)
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(ResultState.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun updateApiService(apiService: ApiService) {
        this.apiService = apiService
    }

    suspend fun getStories(): Flow<ResultState<ListStoryResponse>> = flow {
        try {
            val response = apiService.getStories()
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getDetailStories(id: String): Flow<ResultState<DetailStoryResponse>> = flow {
        try {
            val response = apiService.getDetailStories(id)
            emit(ResultState.Success(response))
        } catch (e: Exception) {
            emit(ResultState.Error(e.message ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    fun uploadImg(imageFile: File, description: String): Flow<ResultState<UploadResponse>> = flow {
        emit(ResultState.Loading)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImg = imageFile.asRequestBody("image/jpg".toMediaType())
        val bodyMultipart = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImg
        )
        try {
            val responseSuccess = apiService.upload(bodyMultipart, requestBody)
            emit(ResultState.Success(responseSuccess))
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            val responseError = Gson().fromJson(errorBody, UploadResponse::class.java)
            emit(ResultState.Error(responseError.message))
        }
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userPreference: UserPreference, apiService: ApiService): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}