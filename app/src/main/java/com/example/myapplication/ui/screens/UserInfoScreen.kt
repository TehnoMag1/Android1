package com.example.myapplication.ui.screens

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.network.ScanQrCodeEntityDto
import com.example.myapplication.data.network.getPhoto
import com.example.myapplication.data.network.model.user.User
import com.example.myapplication.data.network.networkApi
import com.example.myapplication.data.network.uploadPhoto
import com.example.myapplication.ui.theme.tintColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun UserInfoScreen(
    userId: Int,
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val networkApi = remember { networkApi }
    val userSharedDataStore = remember { UserSharedDataStore(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var errorMessage by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }
    var scanQrCodeEntityDtos = remember { mutableStateListOf<ScanQrCodeEntityDto>() }

    var photo by remember { mutableStateOf<Bitmap?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            CoroutineScope(Dispatchers.IO).launch {
                userSharedDataStore.getToken()?.let {
                    val success = uploadPhoto(userId, uri, context, networkApi, "Bearer $it")
                    if (success) {
                        photo = getPhoto(userId, networkApi, "Bearer $it")
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit, block = {
        try {
            Log.e("userId", userId.toString())
            isAdmin = userSharedDataStore.getIsAdmin()
            userSharedDataStore.getToken()?.let {
                val response = networkApi.userGetById(userId, "Bearer $it")

                if(response.body() != null)
                    user = response.body()
                else
                    errorMessage = response.errorBody()?.string() ?: "Error"

                photo = withContext(Dispatchers.IO) { getPhoto(userId, networkApi, "Bearer $it") }

                scanQrCodeEntityDtos.clear()
                scanQrCodeEntityDtos.addAll(networkApi.getScanQrCodes("Bearer $it").filter { it.user.id == userId }.sortedByDescending { it.date })
            }

        }catch (e: Exception) {
            errorMessage = e.message ?: "Error"
        }
    })

    LazyColumn {
        if(errorMessage.isNotEmpty()) item {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                textAlign = TextAlign.Center
            )
        }else if (user == null) item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = tintColor,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }else item {
            TopAppBar(
                backgroundColor = tintColor,
                title = {
                    Text(text = user?.lastName ?: "-", color = Color.White)
                },
                actions = {
                    if(isAdmin) {
                        IconButton(onClick = {
                            user?.id?.let {
                                navController.navigate("users/create_or_update?userId=$it")
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }

                        IconButton(onClick = {
                            scope.launch {
                                try {
                                    userSharedDataStore.getToken()?.let {
                                        val response = networkApi.userDeleteById(userId, "Bearer $it")

                                        if(response.isSuccessful)
                                            navController.navigateUp()
                                        else
                                            Toast.makeText(
                                                context,
                                                response.errorBody()?.string().toString(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                    }
                                }catch (e: Exception) {
                                    Toast.makeText(
                                        context,
                                        e.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                    }
                }
            )

           Column(
               modifier = Modifier.fillMaxWidth(),
               horizontalAlignment = Alignment.CenterHorizontally
           ) {
               if (photo != null) {
                   Image(
                       bitmap = photo!!.asImageBitmap(),
                       contentDescription = "User Photo",
                       modifier = Modifier
                           .size(150.dp)
                           .clip(CircleShape)
                           .border(2.dp, Color.Gray, CircleShape),
                       contentScale = ContentScale.Crop
                   )
               }

               if (isAdmin) {
                   Button(onClick = { launcher.launch("image/*") }) {
                       Text("Выбрать фото")
                   }
               }
           }

            RowContentText(leftText = "Имя", rightText = user?.firstName ?: "-")
            RowContentText(leftText = "Отчество", rightText = user?.midName ?: "-")

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 15.dp, bottom = 5.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Расширенная информация",
                    fontWeight = FontWeight.W900,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                if(isAdmin) {
                    IconButton(onClick = {
                        navController.navigate("user/${userId}/details/update")
                    }) {
                        Icon(
                            imageVector = if(user?.birthday != null)
                                Icons.Default.Edit
                            else
                                Icons.Default.Add,
                            contentDescription = null
                        )
                    }
                }
            }

            user?.birthday?.let {
                RowContentText(
                    leftText = "День рождения",
                    rightText = it
                )
            }

            user?.workshopName?.let {
                RowContentText(
                    leftText = "Название цеха",
                    rightText = it
                )
            }

            user?.workshopNumber?.let {
                RowContentText(
                    leftText = "Номер цеха",
                    rightText = it
                )
            }

            user?.doljnost?.let {
                RowContentText(
                    leftText = "Должность",
                    rightText = it.name
                )
            }

            if (isAdmin && scanQrCodeEntityDtos.isNotEmpty()) {
                Spacer(Modifier.height(15.dp))

                Text(
                    text = "История",
                    fontWeight = FontWeight.W900,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(5.dp))

                LazyRow {
                    items(scanQrCodeEntityDtos) {
                        Card(
                            modifier = Modifier.padding(horizontal = 5.dp),
                            contentColor = Color(0xFFBEB7B7)
                        ) {
                            Text(
                                text = it.date,
                                color = Color.Black,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RowContentText(
    leftText: String,
    rightText: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 5.dp, vertical = 5.dp)
            .clickable {
                if (onClick != null) onClick()
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = leftText,
            modifier = Modifier.padding(5.dp)
        )

        Text(
            text = rightText,
            modifier = Modifier.padding(5.dp),
            textAlign = TextAlign.End
        )
    }

    Divider(color = tintColor)
}