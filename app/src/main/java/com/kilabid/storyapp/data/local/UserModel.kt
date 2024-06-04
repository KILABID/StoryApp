package com.kilabid.storyapp.data.local

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false,
)
