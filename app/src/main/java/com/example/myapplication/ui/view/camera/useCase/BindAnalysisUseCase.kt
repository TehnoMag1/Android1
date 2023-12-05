package com.example.myapplication.ui.view.camera.useCase

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.common.MlKitException
import com.google.mlkit.vision.barcode.common.Barcode
import com.example.myapplication.ui.view.camera.detection.BarcodeScannerProcessor
import com.example.myapplication.ui.view.camera.model.SourceInfo

fun bindAnalysisUseCase(
    lens: Int,
    barcodeScanner:Boolean,
    setSourceInfo: (SourceInfo) -> Unit,
    onBarcodeDetected:(List<Barcode>) -> Unit,
): ImageAnalysis? {

    val barcodeProcessor = try {
        if (barcodeScanner){
            BarcodeScannerProcessor()
        }else{
            null
        }
    }catch (e:Exception){
        Log.e("CAMERA", "Can not create image processor", e)
        return null
    }

    val builder = ImageAnalysis.Builder()
    val analysisUseCase = builder.build()

    var sourceInfoUpdated = false

    analysisUseCase.setAnalyzer(
        TaskExecutors.MAIN_THREAD
    ) { imageProxy: ImageProxy ->
        if (!sourceInfoUpdated) {
            setSourceInfo(obtainSourceInfo(lens, imageProxy))
            sourceInfoUpdated = true
        }
        try {
            barcodeProcessor?.let {
                barcodeProcessor.processImageProxy(imageProxy, onBarcodeDetected)
            }
        } catch (e: MlKitException) {
            Log.e(
                "CAMERA", "Failed to process image. Error: " + e.localizedMessage
            )
        }
    }
    return analysisUseCase
}


private fun obtainSourceInfo(lens: Int, imageProxy: ImageProxy): SourceInfo {
    val isImageFlipped = lens == CameraSelector.LENS_FACING_FRONT
    val rotationDegrees = imageProxy.imageInfo.rotationDegrees
    return if (rotationDegrees == 0 || rotationDegrees == 180) {
        SourceInfo(
            height = imageProxy.height, width = imageProxy.width, isImageFlipped = isImageFlipped
        )
    } else {
        SourceInfo(
            height = imageProxy.width, width = imageProxy.height, isImageFlipped = isImageFlipped
        )
    }
}