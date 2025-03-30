package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.data.network.networkApi
import com.example.myapplication.ui.screens.AuthScreen
import com.example.myapplication.ui.screens.CreateOrUpdateUserScreen
import com.example.myapplication.ui.screens.GenerationQrCodeScreen
import com.example.myapplication.ui.screens.ScanQrCodeScreen
import com.example.myapplication.ui.screens.UserInfoScreen
import com.example.myapplication.ui.screens.UserSharedDataStore
import com.example.myapplication.ui.screens.UserUpdateDetailsScreen
import com.example.myapplication.ui.screens.UsersScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val userSharedDataStore = remember { UserSharedDataStore(this) }

            NavHost(
                navController = navController,
                startDestination = if(userSharedDataStore.getToken() != null)
                    "GenerationQrCodeScreen"
                else
                    "auth",
                builder = {
                    composable("auth") {
                        AuthScreen(navController = navController, onFinishAct = {
                            this@MainActivity.finish()
                        })
                    }

                    composable("scan_qr_code") {
                        ScanQrCodeScreen(
                            navController = navController,
                            networkApi = networkApi
                        )
                    }

                    composable("GenerationQrCodeScreen") {
                        GenerationQrCodeScreen(navController)
                    }

                    composable(
                        route = "user_info/{userId}",
                        arguments = listOf(
                            navArgument("userId"){
                                type = NavType.IntType
                                nullable = false
                            }
                        )
                    ) {
                        UserInfoScreen(
                            userId = it.arguments!!.getInt("userId", 0),
                            navController = navController
                        )
                    }

                    composable("users") {
                        UsersScreen(navController = navController)
                    }

                    composable("users/create_or_update?userId={userId}", arguments = listOf(
                        navArgument("userId"){
                            type = NavType.StringType
                            nullable = true
                        }
                    )) {
                        CreateOrUpdateUserScreen(
                            navController = navController,
                            userId = it.arguments?.getString("userId")?.toIntOrNull()
                        )
                    }

                    composable("user/{userId}/details/update", arguments = listOf(
                        navArgument("userId"){
                            type = NavType.IntType
                            nullable = false
                        }
                    )) {
                        UserUpdateDetailsScreen(
                            navController = navController,
                            userId = it.arguments!!.getInt("userId")
                        )
                    }
                }
            )
        }
    }
}
