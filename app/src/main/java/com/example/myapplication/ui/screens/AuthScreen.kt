package com.example.myapplication.ui.screens

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.myapplication.data.network.NetworkApi
import com.example.myapplication.data.network.model.login.LoginRequest
import com.example.myapplication.data.network.model.login.LoginResponse
import com.example.myapplication.data.network.networkApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val USER_SHARED_KEY_NAME = "user"

class UserSharedDataStore(context: Context) {

    private val shared = context.getSharedPreferences(USER_SHARED_KEY_NAME, Context.MODE_PRIVATE)

    fun getUserId(): Int? {
        return try {
            shared.getInt("user_id", 0)
        }catch (e: Exception) {
            null
        }
    }

    fun getToken(): String? {
        return try {
            shared.getString("token", null)
        }catch (e: Exception) {
            null
        }
    }

    fun getIsAdmin(): Boolean {
        return try {
            shared.getBoolean("is_admin", false)
        }catch (e: Exception) {
            false
        }
    }

    fun save(i: LoginResponse) {
        shared.edit()
            .putString("token", i.accessToken)
            .putInt("user_id", i.userId)
            .putBoolean("is_admin", i.isAdmin)
            .apply()
    }
}

class AuthViewModel(
    private val networkApi: NetworkApi
) {

    suspend fun login(
        key: String,
        onSuccess: (LoginResponse) -> Unit,
        onFailed: (message: String) -> Unit,
        onFinally: () -> Unit
    ) {
        try {
            val response = networkApi.login(LoginRequest(uKey = key))
            if(response.isSuccessful && response.body() != null)
                onSuccess(response.body()!!)
            else
                onFailed(response.errorBody()?.string().toString())
        }catch (e: Exception) {
            onFailed(e.message ?: "error auth")
        }finally {
            onFinally()
        }
    }
}

private sealed interface AuthScreenState {
    object Loading: AuthScreenState
    object Success: AuthScreenState
    data class Failed(val message: String): AuthScreenState
}

@Composable
fun AuthScreen(
    navController: NavController,
    onFinishAct: () -> Unit
) {
    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val userSharedDataStore = remember { UserSharedDataStore(context) }
    var key by remember { mutableStateOf("") }
    var screenState by remember { mutableStateOf<AuthScreenState>(AuthScreenState.Success) }
    val focusRequester = remember(::FocusRequester)

    val viewModel = remember { AuthViewModel(networkApi) }

    BackHandler {
        when(screenState) {
            AuthScreenState.Success -> onFinishAct()
            else -> screenState = AuthScreenState.Success
        }
    }

    LaunchedEffect(key1 = Unit, block = {
        delay(100L)
        focusRequester.freeFocus()
    })

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when(val state = screenState) {
            is AuthScreenState.Failed -> {
                Text(
                    text = state.message,
                    color = Color.Red,
                    fontWeight = FontWeight.W900,
                    fontSize = 20.sp
                )
            }
            AuthScreenState.Loading -> {
                CircularProgressIndicator()
            }
            AuthScreenState.Success -> {
                OutlinedTextField(
                    value = key,
                    onValueChange = { key = it },
                    modifier = Modifier
                        .padding(5.dp)
                        .focusRequester(focusRequester),
                    shape = AbsoluteCutCornerShape(16.dp),
                    label = {
                        Text(text = "Key")
                    }
                )

                AnimatedVisibility(visible = key.isNotEmpty()) {
                    Button(
                        modifier = Modifier.padding(5.dp),
                        onClick = {
                            scope.launch {
                                screenState = AuthScreenState.Loading
                                viewModel.login(key = key.trim(),
                                    onSuccess = {
                                        userSharedDataStore.save(it)
                                        navController.navigate("GenerationQrCodeScreen") {
                                            popUpTo("auth") {
                                                inclusive = true
                                            }
                                        }
                                    }, onFailed = {
                                        screenState = AuthScreenState.Failed(it)
                                    }, onFinally = {}
                                )
                            }
                        }
                    ) {
                        Text(text = "Auth")
                    }
                }
            }
        }
    }
}