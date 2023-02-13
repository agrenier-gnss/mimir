package com.mobilewizards.logging_app

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.net.wifi.rtt.RangingRequest
import android.net.wifi.rtt.RangingResult
import android.net.wifi.rtt.RangingResultCallback
import android.net.wifi.rtt.WifiRttManager
import android.util.Log

class ConnectionHandler {

    protected var context: Context

    constructor(context: Context) : super() {
        this.context = context.applicationContext
    }

    private fun logWifiConnections() {

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiScanReceiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {

                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)

                if (success) {

                    try {
                        val results = wifiManager.scanResults
                        Log.d("Wifi connections", results.toString())
                    } catch (e: SecurityException) {
                        Log.d("Error", "No permissions for fetching wifi")
                    }

                } else {
                    Log.d("Error", "Failed to fetch wifi connections")
                }

            }

        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)

        val success = wifiManager.startScan()
        if (!success) {
            Log.d("Error", "Failed to fetch wifi connections")
        }
    }

    @SuppressLint("MissingPermission")
    private fun logWifiDistance() {

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager


        val broadCastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d("Getting wifi results", "onReceive called")
                for(scanResult in wifiManager.scanResults) {
                    if(scanResult.is80211mcResponder) {
                        val rB = RangingRequest.Builder()
                        rB.addAccessPoint(scanResult)

                        val rttManager = context.getSystemService(Context.WIFI_RTT_RANGING_SERVICE) as WifiRttManager

                        val callback: RangingResultCallback = object : RangingResultCallback() {
                            override fun onRangingResults(results: List<RangingResult>) {
                                // Handle result, e.g. get distance to Access Point

                            }

                            override fun onRangingFailure(code: Int) {
                                // Handle failure
                            }
                        }

                        rttManager.startRanging(rB.build(),
                            context.mainExecutor,
                            callback);
                    }
                }
            }
        }

        context.registerReceiver(broadCastReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))

    }

    @SuppressLint("MissingPermission")
    private fun logBLE() {

        val bluetoothManager= context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter as BluetoothAdapter
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        val scanCallback = object : ScanCallback() {

            override fun onScanResult(callbackType: Int, result: ScanResult?) {

                super.onScanResult(callbackType, result)
                if(result == null || result.device == null) {
                    return
                }

                Log.d("BLE match", result.device.name.toString())

            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                Log.d("BLE batch scan result", "Scanning...")
                super.onBatchScanResults(results)
            }

            override fun onScanFailed(errorCode: Int) {

                Log.d("Error", "BLE onScanFailed: $errorCode")
                super.onScanFailed(errorCode)
            }
        }

        bluetoothLeScanner.startScan(scanCallback)

    }

}