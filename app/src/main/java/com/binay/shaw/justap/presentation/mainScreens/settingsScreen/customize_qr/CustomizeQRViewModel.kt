package com.binay.shaw.justap.presentation.mainScreens.settingsScreen.customize_qr

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.binay.shaw.justap.base.BaseViewModel
import com.binay.shaw.justap.utilities.Constants
import com.binay.shaw.justap.utilities.Logger
import com.binay.shaw.justap.utilities.encodeAsQrCodeBitmap
import com.binay.shaw.justap.utilities.roundedQRGenerator
import com.binay.shaw.justap.model.QRCode


class CustomizeQRViewModel(application: Application): BaseViewModel(application) {

    val qrResultLiveData = MutableLiveData<Bitmap>()
    val qrObjectLiveData = MutableLiveData<QRCode>()

    val defaultPrimaryColor = MutableLiveData<Int>()
    var displayMetricsSize = MutableLiveData<Int>()
    var defaultOverlay = MutableLiveData<Bitmap>()
    val defaultSecondaryColor = MutableLiveData<Int>()

    val selectedOverlayLiveData = MutableLiveData<Bitmap>()

    val savedQRPrimaryColor = MutableLiveData<Int>()
    val savedQRSecondaryColor = MutableLiveData<Int>()
    var savedQRisCircular = MutableLiveData<Boolean>()
    var savedQROverlay = MutableLiveData<Bitmap>()

    val errorMessage = MutableLiveData<String>()
    val qrReset = MutableLiveData<Boolean>()
    val message: String = Constants.myEmail

    init {
        qrReset.value = false
        savedQRisCircular.value = false
    }


    fun setupQRObject(qrObject: QRCode) {
        qrObjectLiveData.value = qrObject
        Logger.debugLog("QR Object: $qrObject")
        generateQR()
    }

    private fun generateQR() {
        val qrObject = qrObjectLiveData.value!!
        Logger.debugLog("QRObject- $qrObject")
        try {
            Logger.debugLog("Inside try")
            qrResultLiveData.value = if (qrObject.isCircular) {
                Logger.debugLog("Inside first case")
                message.roundedQRGenerator(qrObject.displayMetrics!!, qrObject.overlay, qrObject.primaryColor, qrObject.secondaryColor)
            } else {
                Logger.debugLog("Inside second case")
                Logger.debugLog("DisplayMetrics ${displayMetricsSize.value}")
                Logger.debugLog("Overlay: ${qrObject.overlay}")
                Logger.debugLog("Primary: ${qrObject.primaryColor}")
                Logger.debugLog("Secondary: ${qrObject.secondaryColor}")
                message.encodeAsQrCodeBitmap(qrObject.displayMetrics!!, qrObject.overlay, qrObject.primaryColor, qrObject.secondaryColor)
            }

        } catch (e: Exception) {
            Logger.debugLog("Exception: $e")
            errorMessage.value = e.message.toString()
        }
    }


}