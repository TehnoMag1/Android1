package com.example.myapplication.data.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.example.myapplication.data.network.model.login.LoginRequest
import com.example.myapplication.data.network.model.login.LoginResponse
import com.example.myapplication.data.network.model.user.CreateUserDetailsParams
import com.example.myapplication.data.network.model.user.CreateOrUpdateUserParams
import com.example.myapplication.data.network.model.user.Doljnost
import com.example.myapplication.data.network.model.user.User
import com.example.myapplication.data.network.model.user.UserShort
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate

interface NetworkApi {

    @POST("/EL-documents/api/v1/users/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>

    @GET("/EL-documents/api/v1/users/me")
    suspend fun getMe(
        @Header("Authorization") token: String
    ): Response<User>

    @GET("/EL-documents/api/v1/users/{id}")
    suspend fun userGetById(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Response<User>

    @POST("/EL-documents/api/v1/users")
    suspend fun createUser(
        @Body body: CreateOrUpdateUserParams,
        @Header("Authorization") token: String
    ): Response<Unit?>

    @PUT("/EL-documents/api/v1/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body body: CreateOrUpdateUserParams,
        @Header("Authorization") token: String
    ): Response<Unit?>

    @GET("/EL-documents/api/v1/users/")
    suspend fun userGetAll(
        @Header("Authorization") token: String
    ): Response<List<UserShort>>

    @POST("/EL-documents/api/v1/users/{id}/details")
    suspend fun createUserDetails(
        @Path("id") id: Int,
        @Body body: CreateUserDetailsParams,
        @Header("Authorization") token: String
    ): Response<Unit?>

    @DELETE("/EL-documents/api/v1/users/{id}")
    suspend fun userDeleteById(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Response<Unit?>

    @GET("/EL-documents/api/v1/doljnosti/")
    suspend fun getAllDoljnosti(
        @Header("Authorization") token: String
    ): Response<List<Doljnost>>

    @Multipart
    @POST("/EL-documents/api/v1/users/{id}/photo")
    suspend fun uploadPhoto(
        @Path("id") userId: Int,
        @Part file: MultipartBody.Part,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @GET("/EL-documents/api/v1/users/{id}/photo")
    suspend fun getPhoto(
        @Path("id") userId: Int,
        @Header("Authorization") token: String
    ): Response<ResponseBody>

    @POST("/EL-documents/api/v1/users/scan-qr-codes")
    suspend fun createScanQrCodes(
        @Query("userId") userId: Int,
        @Header("Authorization") token: String
    ): Response<Unit?>

    @GET("/EL-documents/api/v1/users/scan-qr-codes")
    suspend fun getScanQrCodes(
        @Header("Authorization") token: String
    ): List<ScanQrCodeEntityDto>
}

data class ScanQrCodeEntityDto(
    val id: Int,
    val date: String,
    val user: User
) {
    data class User(
        val id: Int
    )
}

suspend fun uploadPhoto(
    userId: Int,
    uri: Uri,
    context: Context,
    networkApi: NetworkApi,
    token: String
): Boolean {
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri) ?: return false
    val byteArray = inputStream.readBytes()

    val requestBody = byteArray.toRequestBody("image/*".toMediaTypeOrNull())
    val part = MultipartBody.Part.createFormData("file", "photo.jpg", requestBody)

    val response = networkApi.uploadPhoto(userId, part, token)
    return response.isSuccessful
}

suspend fun getPhoto(userId: Int, networkApi: NetworkApi, token: String): Bitmap? {
    val response = networkApi.getPhoto(userId, token)
    return if (response.isSuccessful) {
        BitmapFactory.decodeStream(response.body()?.byteStream())
    } else {
        null
    }
}

private val retrofit = Retrofit.Builder()
    .baseUrl("https://map.matstart.ru:30")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val networkApi = retrofit.create<NetworkApi>()

