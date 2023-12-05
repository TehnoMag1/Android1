package com.example.myapplication.data.network.model.login

data class LoginResponse(
    val accessToken: String,
    val userId: Int
)

data class LoginRequest(
    val uKey: String
)