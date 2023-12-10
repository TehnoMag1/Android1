package com.example.myapplication.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.data.network.model.user.UserShort
import com.example.myapplication.data.network.networkApi

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UsersScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val networkApi = remember { networkApi }
    val userDataStore = remember { UserSharedDataStore(context) }
    var users by remember { mutableStateOf(emptyList<UserShort>()) }
    val isAdmin by remember { mutableStateOf(userDataStore.getIsAdmin()) }

    LaunchedEffect(key1 = Unit, block = {
        val token = userDataStore.getToken()
        val response = networkApi.userGetAll("Bearer $token")
        Log.e("networkApi", response.errorBody()?.string().toString())
        response.body()?.let { users = it }
    })

    LazyColumn {
        item {
            if(isAdmin) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { navController.navigate("users/create_or_update") }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }
            }
        }
        items(users) {user ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 5.dp),
                shape = AbsoluteRoundedCornerShape(15.dp),
                elevation = 9.dp,
                onClick = { navController.navigate("user_info/${user.id}") }
            ) {
                Text(
                    text = "${user.lastName} ${user.firstName} ${user.midName}",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}