package com.mobilewizards.watchlogger

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.wearable.Wearable
import com.mobilewizards.logging_app.MainActivityWatch
import java.io.File

//Tänne talletettavia tietoja, esim asetukset ja filepath?
object WatchActivityHandler {

    private var filepath = ""
    private val TAG = "watchLogger"
    var fileSendOk : Boolean = false

    fun getFilePath(filePath: String){

        filepath = filePath

    }


    fun sendFileToPhone(){


    }

    fun fileSendStatus(fileSend: Boolean){

        fileSendOk = fileSend

    }

    fun checkFileSend(): Boolean {

        return fileSendOk
    }

    fun giveFilePath(): String {
        return filepath
    }

}