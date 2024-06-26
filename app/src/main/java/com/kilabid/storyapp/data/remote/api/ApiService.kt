package com.kilabid.storyapp.data.remote.api

import com.kilabid.storyapp.data.remote.response.DetailStoryResponse
import com.kilabid.storyapp.data.remote.response.ListStoryResponse
import com.kilabid.storyapp.data.remote.response.LoginResponse
import com.kilabid.storyapp.data.remote.response.RegisterResponse
import com.kilabid.storyapp.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
    ): LoginResponse

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): RegisterResponse


    @GET("stories/{id}")
    suspend fun getDetailStories(
        @Path("id") id: String,
    ): DetailStoryResponse


    @Multipart
    @POST("stories")
    suspend fun upload(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ): UploadResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location : Int = 1,
    ): ListStoryResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): ListStoryResponse
}