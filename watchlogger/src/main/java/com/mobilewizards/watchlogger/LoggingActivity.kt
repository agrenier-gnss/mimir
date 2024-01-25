package com.mobilewizards.logging_app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mimir.sensors.LoggingService
import com.mobilewizards.logging_app.databinding.ActivityLoggingBinding
import com.mobilewizards.watchlogger.WatchActivityHandler
import java.io.Serializable


var startTime: Long = 0

// =================================================================================================

class LoggingActivity : Activity() {

    private lateinit var binding: ActivityLoggingBinding

    private val durationHandler = Handler()

    // Logging service
    lateinit var loggingIntent : Intent

    // Components
    private lateinit var startLogBtn : Button
    private lateinit var stopLogBtn  : Button
    private lateinit var logText     : TextView
    private lateinit var logTimeText : TextView

    // ---------------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoggingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        this.checkPermissions()

        // Set views
        startLogBtn = findViewById(R.id.startLogBtn)
        stopLogBtn  = findViewById(R.id.stopLogBtn)
        logText     = findViewById(R.id.logInfoText)
        logTimeText = findViewById(R.id.logTimeText)
        startLogBtn.visibility = View.VISIBLE
        stopLogBtn.visibility = View.GONE
        logText.visibility = View.GONE
        logTimeText.visibility = View.GONE

        // Set service
        loggingIntent = Intent(this, LoggingService::class.java)

        // starts logging
        startLogBtn.setOnClickListener{
            startLogging(this)
        }

        // stops logging
        stopLogBtn.setOnClickListener{
            stopLogging()
        }

//        // Initialize the variable sensorManager
//        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
//        // getSensorList(Sensor.TYPE_ALL) lists all the sensors present in the device
//        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
//
//        for (sensors in deviceSensors) {
//            Log.d("sensors", sensors.toString() + "\n")
//        }
    }

    // ---------------------------------------------------------------------------------------------

    fun startLogging(context: Context){

        // Set duration timer
        startTime = SystemClock.elapsedRealtime()
        updateDurationText()
        durationHandler.postDelayed(updateRunnableDuration, 1000)

        // Set buttons
        startLogBtn.visibility = View.GONE
        stopLogBtn.visibility = View.VISIBLE
        logText.visibility = View.VISIBLE
        logText.text = "Surveying..."
        logTimeText.visibility = View.VISIBLE

        // Set the data to be sent to service
        loggingIntent.putExtra("settings", WatchActivityHandler.sensorsSelected as Serializable)

        // Start service
        ContextCompat.startForegroundService(this, loggingIntent)
    }

    // ---------------------------------------------------------------------------------------------

    fun stopLogging(){

        stopLogBtn.visibility = View.GONE
        logTimeText.visibility = View.GONE
        logText.text = "Survey ended"

        // Stop logging service
        stopService(loggingIntent)

        // Wait 2 seconds an go back to main screen
        Handler(Looper.getMainLooper()).postDelayed({
            finish()
        }, 2000)
    }

    // ---------------------------------------------------------------------------------------------

    fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.HIGH_SAMPLING_RATE_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.FOREGROUND_SERVICE_LOCATION
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

    // ---------------------------------------------------------------------------------------------

    private val updateRunnableDuration = object : Runnable {
        override fun run() {
            // Update the duration text every second
            updateDurationText()

            // Schedule the next update
            durationHandler.postDelayed(this, 1000)
        }
    }

    private fun updateDurationText() {
        // Calculate the elapsed time since the button was clicked
        val currentTime = SystemClock.elapsedRealtime()
        val elapsedTime = currentTime - startTime

        // Format the duration as HH:MM:SS
        val hours = (elapsedTime / 3600000).toInt()
        val minutes = ((elapsedTime % 3600000) / 60000).toInt()
        val seconds = ((elapsedTime % 60000) / 1000).toInt()

        // Display the formatted duration in the TextView
        val durationText = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        logTimeText.text = "$durationText"
    }

    override fun onDestroy() {
        // Remove the updateRunnable when the activity is destroyed to prevent memory leaks
        durationHandler.removeCallbacks(updateRunnableDuration)
        super.onDestroy()
    }
}