package com.example.myapplication.data.network

import com.example.myapplication.data.network.model.login.LoginRequest
import com.example.myapplication.data.network.model.login.LoginResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.POST

interface NetworkApi {

    @POST("/api/v1/users/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>

}


private val retrofit = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:8080")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val networkApi = retrofit.create<NetworkApi>()

