package com.example.myapplication.ui.screens

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

@Composable
fun GenerationQrCodeScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val userSharedDataStore = remember { UserSharedDataStore(context) }
    val uKey by remember { mutableStateOf(userSharedDataStore.getUserId()) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        generateBarCode(uKey.toString())?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null
            )
        }

        Button(onClick = { navController.navigate("scan_qr_code") }) {
            Text(text = "Scan qr code")
        }
    }
}

fun generateBarCode(
    text:String,
    format: BarcodeFormat = BarcodeFormat.QR_CODE
): Bitmap? {
    return try {

        val width = 1000
        val height = 600
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()

        val bitMatrix = codeWriter.encode(
            text,
            format,
            width,
            height
        )

        for (x in 0 until width) {
            for (y in 0 until height) {
                val color = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                bitmap.setPixel(x, y, color)
            }
        }

        bitmap
    }catch (e:Exception){ null }
}