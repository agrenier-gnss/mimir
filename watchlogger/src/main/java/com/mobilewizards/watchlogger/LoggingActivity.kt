package com.mobilewizards.logging_app

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.mobilewizards.logging_app.databinding.ActivityLoggingBinding
import com.mobilewizards.watchlogger.*
import java.time.LocalDateTime

var startTime: Long? = null

class LoggingActivity : Activity() {

    private lateinit var binding: ActivityLoggingBinding
    var imuFrequency: Int = 10
    var magnetometerFrequency: Int = 1
    var barometerFrequency: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoggingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.checkPermissions()

        val ble =  WatchBLEHandler(this)
        val imu = WatchMotionSensorsHandler(this)
        val gnss = WatchGNSSHandler(this)
        val ecg = HealthServicesHandler(this)

        // Fetching file path for sendCsvToPhone function
        var filePath = ""
        fun getRealPathFromUri(contentResolver: ContentResolver, uri: Uri): String {
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(uri, projection, null, null, null)
            val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor?.moveToFirst()
            val path = columnIndex?.let { cursor?.getString(it) }
            cursor?.close()
            return path ?: ""
        }

        val startLogBtn = findViewById<Button>(R.id.startLogBtn)
        startLogBtn.visibility = View.VISIBLE

        val stopLogBtn = findViewById<Button>(R.id.stopLogBtn)
        stopLogBtn.visibility = View.GONE

        val reviewBtn = findViewById<Button>(R.id.reviewBtn)
        reviewBtn.visibility = View.GONE

        val logText =  findViewById<TextView>(R.id.logInfoText)
        logText.visibility = View.GONE

        val logTimeText =  findViewById<TextView>(R.id.logTimeText)
        logTimeText.visibility = View.GONE

        // starts logging
        startLogBtn.setOnClickListener{

            startTime = System.currentTimeMillis()
            val currentTime = LocalDateTime.now()

            startLogBtn.visibility = View.GONE
            stopLogBtn.visibility = View.VISIBLE
            logText.visibility = View.VISIBLE
            logText.text = "Surveying..."
            logTimeText.visibility = View.VISIBLE
            logTimeText.text = currentTime.toString()

            startTime = System.currentTimeMillis()

            WatchActivityHandler.clearFilfPaths()

            if (WatchActivityHandler.getImuStatus()) {
                imu.setUpSensors(
                    imuFrequency,magnetometerFrequency,barometerFrequency
                )
            }

            if (WatchActivityHandler.getGnssStatus()) {
            gnss.setUpLogging()
            }

            ble.setUpLogging()

            if (WatchActivityHandler.getEcgStatus()) {
            ecg.getHeartRate()
            }
        }

        // stops logging
        stopLogBtn.setOnClickListener{
            reviewBtn.visibility = View.VISIBLE
            stopLogBtn.visibility = View.GONE
            logTimeText.visibility = View.GONE
            logText.text = "Survey ended"
            if (imu.isLogging) {
                imu.stopLogging()
            }

            if (gnss.isLogging) {
                gnss.stopLogging(this)
            }

            ble.stopLogging()

            if (ecg.isLogging) {
                ecg.stopHeartRate()
            }
        }

        // Opens LoggedEvent.kt for deciding what to do with the logged events
        reviewBtn.setOnClickListener{
            val openLoading = Intent(applicationContext, LoggedEvent::class.java)
            startActivity(openLoading)
        }

    }

    fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.BODY_SENSORS,
        )

        var allPermissionsGranted = true
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false
                break
            }
        }

        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, 225)
        }
    }

}