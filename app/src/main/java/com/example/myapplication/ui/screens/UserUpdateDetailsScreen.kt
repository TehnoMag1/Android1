package com.example.myapplication.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.network.model.user.CreateUserDetailsParams
import com.example.myapplication.data.network.model.user.Doljnost
import com.example.myapplication.data.network.networkApi
import com.example.myapplication.ui.theme.tintColor
import com.example.myapplication.ui.view.BaseOutlinedTextField
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UserUpdateDetailsScreen(
    userId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val networkApi = remember { networkApi }
    val userSharedDataStore = remember { UserSharedDataStore(context) }
    var birthday by remember { mutableStateOf("") }
    var npassport by remember { mutableStateOf("") }
    var spassport by remember { mutableStateOf("") }
    var doljnostId by remember { mutableStateOf<Int?>(null) }
    var doljnosti by remember { mutableStateOf<List<Doljnost>>(emptyList()) }

    LaunchedEffect(key1 = Unit, block = {
        userId.let { id ->
            userSharedDataStore.getToken()?.let { token ->
                networkApi.userGetById(id, "Bearer $token").body()?.let {
                    birthday = it.birthday ?: return@let
                    npassport = it.npassport.toString()
                    spassport = it.spassport.toString()
                    doljnostId = it.doljnost?.id
                }

                networkApi.getAllDoljnosti("Bearer $token").body()?.let {
                    doljnosti = it
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
                value = birthday,
                onValueChange = { birthday = it },
                label = "День рождения"
            )

            BaseOutlinedTextField(
                value = npassport,
                onValueChange = { npassport = it },
                label = "Номер паспорта",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            BaseOutlinedTextField(
                value = spassport,
                onValueChange = { spassport = it },
                label = "Серия паспорта",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Text(
                text = "Должность",
                fontWeight = FontWeight.W900,
                fontSize = 24.sp,
                modifier = Modifier.padding(vertical = 15.dp).fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            LazyRow {
                items(doljnosti) {
                    Card(
                        modifier = Modifier.padding(5.dp),
                        shape = AbsoluteRoundedCornerShape(15.dp),
                        elevation = 9.dp,
                        border = if(doljnostId == it.id) BorderStroke(2.dp, tintColor) else null,
                        onClick = { doljnostId = it.id }
                    ) {
                        Text(
                            text = it.name,
                            modifier = Modifier.padding(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = {
                scope.launch {
                    try {
                        val body = CreateUserDetailsParams(
                            birthday = birthday,
                            doljnostId = doljnostId!!,
                            npassport = npassport.toInt(),
                            spassport = spassport.toInt()
                        )
                        val token = userSharedDataStore.getToken()
                        val response = networkApi.createUserDetails(userId, body, "Bearer $token")
                        Log.e("networkApi", response.errorBody()?.string().toString())
                        if(response.isSuccessful)
                            navController.navigateUp()
                        else
                            Toast.makeText(
                                context,
                                response.errorBody()?.string().toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                    }catch (e: Exception) {
                        Toast.makeText(
                            context,
                            e.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }) {
                Text(text = "Сохранить")
            }
        }
    }
}