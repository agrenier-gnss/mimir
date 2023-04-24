package com.mobilewizards.logging_app

import android.app.Activity
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.ContentValues
import android.content.Context
import android.graphics.Color.green
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar

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

    private var bleScanList = mutableListOf<String>()
    private fun initializeScanCallback() {
        scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                result?.let { scanResult ->
                    val measurementsList = mutableListOf<String>()
                    val device = scanResult.device
                    val rssi = scanResult.rssi
                    val data = scanResult.scanRecord

                    val measurementString =
                        "$device," +
                        "$rssi," +
                        "$data"

                    measurementsList.add(measurementString)
                    bleScanList.addAll(measurementsList)

                    Log.i("BleLogger", "Device: ${device.address} RSSI: $rssi Data: ${data?.bytes.contentToString()}")
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

            val contentValues = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, "bluetooth_measurements.csv")
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }

            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

            Log.d("uri", uri.toString())
            uri?.let { mediaUri ->
                context.contentResolver.openOutputStream(mediaUri)?.use { outputStream ->
                    outputStream.write("Device,RSSI,Data\n".toByteArray())
                    bleScanList.forEach { measurementString ->
                        outputStream.write("$measurementString\n".toByteArray())
                    }
                    outputStream.flush()
                }

                val view = (context as Activity).findViewById<View>(android.R.id.content)
                val snackbar = Snackbar.make(view, "Bluetooth scan results saved to Downloads folder", Snackbar.LENGTH_LONG)
                snackbar.setAction("Close") {
                    snackbar.dismiss()
                }
                snackbar.view.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                snackbar.show()

            }

        } catch(e: SecurityException){
            Log.e("Error", "No permission for BLE fetching")
            val view = (context as Activity).findViewById<View>(android.R.id.content)
            val snackbar = Snackbar.make(view, "Error. BLE does not have required permissions.", Snackbar.LENGTH_LONG)
            snackbar.setAction("Close") {
                snackbar.dismiss()
            }
            snackbar.view.setBackgroundColor(ContextCompat.getColor(context, R.color.red))
            snackbar.show()
        }
    }

}