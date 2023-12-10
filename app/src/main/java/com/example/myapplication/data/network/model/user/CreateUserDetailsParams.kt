package com.example.myapplication.data.network.model.user

data class CreateUserDetailsParams(
    val birthday: String,
    val doljnostId: Int,
    val npassport: Int,
    val spassport: Int
)