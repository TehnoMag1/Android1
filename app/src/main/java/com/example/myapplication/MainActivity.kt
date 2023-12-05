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
import com.example.myapplication.ui.screens.AuthScreen
import com.example.myapplication.ui.screens.GenerationQrCodeScreen
import com.example.myapplication.ui.screens.ScanQrCodeScreen
import com.example.myapplication.ui.screens.UserInfoScreen
import com.example.myapplication.ui.screens.UserSharedDataStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val userSharedDataStore = remember { UserSharedDataStore(this) }

            NavHost(
                navController = navController,
                startDestination = if(userSharedDataStore.getIsAuth())
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
                        ScanQrCodeScreen(navController = navController)
                    }

                    composable("GenerationQrCodeScreen") {
                        GenerationQrCodeScreen(navController)
                    }

                    composable(
                        route = "user_info/{uKey}",
                        arguments = listOf(
                            navArgument("uKey"){
                                type = NavType.StringType
                                nullable = false
                            }
                        )
                    ) {
                        UserInfoScreen(
                            uKey = it.arguments!!.getString("uKey", "")
                        )
                    }
                }
            )
        }
    }
}
