package com.example.myapplication.data.network

import com.example.myapplication.data.network.model.login.LoginRequest
import com.example.myapplication.data.network.model.login.LoginResponse
import com.example.myapplication.data.network.model.user.CreateUserDetailsParams
import com.example.myapplication.data.network.model.user.CreateOrUpdateUserParams
import com.example.myapplication.data.network.model.user.Doljnost
import com.example.myapplication.data.network.model.user.User
import com.example.myapplication.data.network.model.user.UserShort
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NetworkApi {

    @POST("/api/v1/users/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>

    @GET("/api/v1/users/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): Response<User>

    @GET("/api/v1/users/{id}")
    suspend fun userGetById(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Response<User>

    @POST("/api/v1/users")
    suspend fun createUser(
        @Body body: CreateOrUpdateUserParams,
        @Header("Authorization") token: String
    ): Response<Unit?>

    @PUT("/api/v1/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body body: CreateOrUpdateUserParams,
        @Header("Authorization") token: String
    ): Response<Unit?>

    @GET("/api/v1/users/")
    suspend fun userGetAll(
        @Header("Authorization") token: String
    ): Response<List<UserShort>>

    @POST("/api/v1/users/{id}/details")
    suspend fun createUserDetails(
        @Path("id") id: Int,
        @Body body: CreateUserDetailsParams,
        @Header("Authorization") token: String
    ): Response<Unit?>

    @DELETE("/api/v1/users/{id}")
    suspend fun userDeleteById(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Response<Unit?>

    @GET("/api/v1/doljnosti/")
    suspend fun getAllDoljnosti(
        @Header("Authorization") token: String
    ): Response<List<Doljnost>>
}


private val retrofit = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:8080")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val networkApi = retrofit.create<NetworkApi>()

