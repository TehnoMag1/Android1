package com.example.myapplication.ui.view.camera

import androidx.camera.core.CameraSelector
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.tintColor

@Composable
fun CameraControlsView(
    onLensChange: () -> Unit,
    onLongClick: () -> Unit,
    onAdsButtonClick: () -> Unit,
    onGenerationQrCodeButtonClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { onLongClick() })
            },
        contentAlignment = Alignment.BottomCenter,
    ) {
        Row {
           Button(
              onClick = onLensChange,
               modifier = Modifier.wrapContentSize().padding(5.dp),
               colors = ButtonDefaults.buttonColors(
                   backgroundColor = tintColor
               )
           ) { Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Switch camera") }
        }
    }
}

fun switchLens(lens: Int) = if (CameraSelector.LENS_FACING_FRONT == lens) {
    CameraSelector.LENS_FACING_BACK
} else {
    CameraSelector.LENS_FACING_FRONT
}