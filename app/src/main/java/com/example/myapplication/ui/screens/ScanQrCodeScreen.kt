package com.example.myapplication.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.data.network.NetworkApi
import com.example.myapplication.ui.view.camera.BarcodeScanner
import com.example.myapplication.ui.view.camera.CameraControlsView
import com.example.myapplication.ui.view.camera.switchLens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanQrCodeScreen(
    navController: NavController,
    networkApi: NetworkApi
) {
    val context = LocalContext.current
    val userSharedDataStore = remember { UserSharedDataStore(context) }
    var qrCodeText by remember { mutableStateOf("") }
    val permissions = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.CAMERA
        )
    )

    LaunchedEffect(key1 = Unit, block = {
        permissions.launchMultiplePermissionRequest()
    })

    if(qrCodeText.isNotEmpty()) {
        LaunchedEffect(key1 = Unit, block = {
            if(qrCodeText.toIntOrNull() == null) {
                Toast.makeText(context, "Неверный qr-код", Toast.LENGTH_SHORT).show()
                qrCodeText = ""
            }else {
                userSharedDataStore.getToken()?.let {
                    val response = networkApi.createScanQrCodes(
                        userId = qrCodeText.toInt(),
                        token = "Bearer $it"
                    )
                    if (response.isSuccessful) {
                        navController.navigate("user_info/$qrCodeText")
                    }else {
                        Toast.makeText(context, response.code().toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }else {
        if (permissions.allPermissionsGranted){
            Box(modifier = Modifier.fillMaxSize()) {
                var lens by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
                BarcodeScanner(
                    cameraLens = lens,
                    barcodeScanner = true
                ){ barcodes, sourceInfo ->
                    barcodes?.let {

                        if(barcodes.firstOrNull()?.rawValue != null){
                            qrCodeText = barcodes.first().rawValue ?: ""
                        }

                        Canvas(
                            modifier = Modifier.fillMaxSize()
                        ){
                            for (barcode in barcodes){
                                val needToMirror = sourceInfo.isImageFlipped
                                val corners = barcode.cornerPoints

                                corners?.let {
                                    drawPath(
                                        path = Path().apply {
                                            corners.forEachIndexed { index, point ->
                                                if (index == 0) {
                                                    if (needToMirror){
                                                        moveTo(size.width - point.x, point.y.toFloat())
                                                    }else {
                                                        moveTo(point.x.toFloat(), point.y.toFloat())
                                                    }
                                                } else {
                                                    if (needToMirror){
                                                        lineTo(size.width - point.x, point.y.toFloat())
                                                    }else{
                                                        lineTo(point.x.toFloat(), point.y.toFloat())
                                                    }
                                                }
                                            }
                                        },
                                        color = Color.Red,
                                        style = Stroke(5f)
                                    )
                                }
                            }
                        }
                    }
                }

                CameraControlsView(
                    onLensChange = { navController.navigateUp() },
                    onLongClick = {},
                    onAdsButtonClick = {

                    },
                    onGenerationQrCodeButtonClick = {
                        navController.navigate("GenerationQrCodeScreen")
                    }
                )
            }
        }else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Разрешите доступ к камере",
                    fontWeight = FontWeight.W900,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(10.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}