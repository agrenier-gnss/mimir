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
    var collectGnns : Boolean = true
    var collectImu : Boolean = true
    var collectEcg : Boolean = true
    var selectedFrequency = 5

    fun getFilePath(filePath: String){

        filepath = filePath

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

    fun changeGnssStatus(status: Boolean) {

        collectGnns = status

    }
    fun getGnssStatus() : Boolean {

        return collectGnns

    }
    fun changeImuStatus(status: Boolean) {

        collectImu = status

    }
    fun getImuStatus() : Boolean {

        return collectImu

    }
    fun changeEcgStatus(status: Boolean) {

        collectEcg = status

    }
    fun getEcgStatus() : Boolean {

        return collectEcg

    }
    fun changeFrequency(time: Int){

        selectedFrequency = time
    }

    fun getFrequency() : Int {

        return selectedFrequency

    }

}