package com.example.myapplication.data.network.model.user

data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val midName: String,
    val ukey: String,
    val birthday: String?,
    val workshopName: String?,
    val workshopNumber: String?,
    val doljnost: Doljnost?,
)

data class Doljnost(
    val id: Int,
    val name: String
)

data class Ukey(
    val ukey: String
)