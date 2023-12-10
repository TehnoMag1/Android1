package com.example.myapplication.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.network.model.user.CreateOrUpdateUserParams
import com.example.myapplication.data.network.networkApi
import com.example.myapplication.ui.view.BaseOutlinedTextField
import kotlinx.coroutines.launch

@Composable
fun CreateOrUpdateUserScreen(
    userId: Int?,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val networkApi = remember { networkApi }
    val userSharedDataStore = remember { UserSharedDataStore(context) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var midName by remember { mutableStateOf("") }
    var ukey by remember { mutableStateOf("") }

    LaunchedEffect(key1 = Unit, block = {
        userId?.let { id ->
            userSharedDataStore.getToken()?.let { token ->
                networkApi.userGetById(id, "Bearer $token").body()?.let {
                    firstName = it.firstName
                    lastName = it.lastName
                    midName = it.midName
                    ukey = it.ukey
                }
            }
        }
    })

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {

            Spacer(modifier = Modifier.height(100.dp))

            BaseOutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "Имя"
            )

            BaseOutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Фамилия"
            )

            BaseOutlinedTextField(
                value = midName,
                onValueChange = { midName = it },
                label = "Отчество"
            )

            BaseOutlinedTextField(
                value = ukey,
                onValueChange = { ukey = it },
                label = "uKey"
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = { scope.launch {
                try {
                    val token = userSharedDataStore.getToken()!!
                    val body = CreateOrUpdateUserParams(
                        firstName, lastName, midName, ukey
                    )
                    val response = if(userId == null)
                        networkApi.createUser(body, "Bearer $token")
                    else
                        networkApi.updateUser(userId, body, "Bearer $token")

                    if(response.isSuccessful) {
                        navController.navigateUp()
                    }else {
                        Toast.makeText(
                            context,
                            response.errorBody()?.string().toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            } }) {
                Text(text = "Сохранить")
            }
        }
    }
}