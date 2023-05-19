package com.mobilewizards.watchlogger

import android.util.Log
import java.io.File

object WatchActivityHandler {

    // Variable to save the files for sending
    private var filepaths = mutableListOf<File>()
    private val TAG = "watchLogger"
    var fileSendOk : Boolean = false
    var collectGnns : Boolean = true
    var collectImu : Boolean = true
    var collectEcg : Boolean = true
    var selectedFrequency = 5

    fun setFilePaths (filePath: File){
        Log.d(TAG, "adding file")
        filepaths.add(filePath)
    }

    fun clearFilfPaths() {
        filepaths.clear()
    }
    fun fileSendStatus(fileSend: Boolean){
        fileSendOk = fileSend
    }

    fun checkFileSend(): Boolean {
        return fileSendOk
    }

    fun getFilePaths(): List<File> {
        return filepaths
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