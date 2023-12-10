package com.example.myapplication.data.network.model.login

data class LoginResponse(
    val accessToken: String,
    val userId: Int,
    val isAdmin: Boolean
)

data class LoginRequest(
    val uKey: String
)