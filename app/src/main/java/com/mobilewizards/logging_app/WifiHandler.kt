package com.mobilewizards.logging_app

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log

class WifiHandler(private val context: Context) : Thread() {

    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var myThread: Thread? = null

    fun startThread() {
        myThread = Thread(Runnable {
            while (!Thread.currentThread().isInterrupted) {
                Thread.sleep(1000)
                logWifiRssi()
            }
        })
        myThread?.start()
    }

    fun stopThread() {
        myThread?.interrupt()
        myThread = null
    }

    fun logWifiRssi() {
        val network = connectivityManager.activeNetwork
        if (network != null) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            if (networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                val wifiRssi = networkCapabilities?.signalStrength
                Log.d("WifiRssiLogger", "WiFi RSSI: $wifiRssi")
            } else {
                Log.d("WifiRssiLogger", "Not connected to a WiFi network")
            }
        } else {
            Log.d("WifiRssiLogger", "No active network")
        }
    }
}