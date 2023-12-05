package com.example.myapplication.ui.view.camera.detection

import android.annotation.SuppressLint
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.TaskExecutors
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarcodeScannerProcessor {

    private val barcodeScanning:BarcodeScanner

    private val executor = TaskExecutors.MAIN_THREAD

    init {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()

        barcodeScanning = BarcodeScanning.getClient(options)
    }

    fun stop(){
        barcodeScanning.close()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun processImageProxy(
        image:ImageProxy,
        onDetectionFinished:(List<Barcode>) -> Unit
    ){
        barcodeScanning.process(InputImage.fromMediaImage(image.image!!, image.imageInfo.rotationDegrees))
            .addOnSuccessListener(executor) { onDetectionFinished(it) }
            .addOnCompleteListener { image.close() }
    }
}