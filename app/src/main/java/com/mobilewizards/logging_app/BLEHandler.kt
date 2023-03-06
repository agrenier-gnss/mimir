package com.mobilewizards.logging_app

import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Handler
import android.util.Log

class BLEHandler(private val context: Context) {

    private lateinit var bluetoothAdapter: BluetoothAdapter
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private lateinit var scanCallback: ScanCallback
    private val handler = Handler()

    init {
        initializeBluetooth()
        initializeScanCallback()
    }

    private fun initializeBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
    }

    private fun initializeScanCallback() {
        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result?.let { scanResult ->
                    val device = scanResult.device
                    val rssi = scanResult.rssi
                    val data = scanResult.scanRecord?.bytes
                    Log.i("BleLogger", "Device: ${device.address} RSSI: $rssi Data: ${data?.contentToString()}")
                }
            }

            override fun onScanFailed(errorCode: Int) {
                Log.e("BleLogger", "Scan failed with error code $errorCode")
            }
        }
    }

    fun setUpLogging() {
        try {
            bluetoothLeScanner?.startScan(scanCallback)
        } catch(e: SecurityException){
            Log.d("Error", "No permission for BLE fetching")
        }
    }

    fun stopLogging() {
        try {
            bluetoothLeScanner?.stopScan(scanCallback)
        } catch(e: SecurityException){
            Log.d("Error", "No permission for BLE fetching")
        }
    }

}