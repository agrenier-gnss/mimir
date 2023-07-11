package com.mobilewizards.logging_app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.mimir.sensors.SensorType
import com.mobilewizards.logging_app.databinding.ActivityLoggingBinding
import com.mobilewizards.watchlogger.*
import java.time.LocalDateTime

import com.mimir.sensors.SensorsHandler
import org.w3c.dom.Text

var startTime: Long? = null

// =================================================================================================

class LoggingActivity : Activity() {

    private lateinit var binding: ActivityLoggingBinding
    var IMUFrequency: Int = 10
    var magnetometerFrequency: Int = 1
    var barometerFrequency: Int = 1
    var healthSensorFrequency: Int = 1

    lateinit var sensorsHandler : SensorsHandler
    private var isLogging: Boolean = false

    private lateinit var startLogBtn : Button
    private lateinit var stopLogBtn  : Button
    private lateinit var reviewBtn   : Button
    private lateinit var logText     : TextView
    private lateinit var logTimeText : TextView

    // ---------------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoggingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        this.checkPermissions()

//        val ble =  WatchBLEHandler(this)
//        val imu = WatchMotionSensorsHandler(this)
//        val gnss = WatchGNSSHandler(this)
//        val ecg = HealthServicesHandler(this)

        // Set views
        startLogBtn = findViewById(R.id.startLogBtn)
        stopLogBtn  = findViewById(R.id.stopLogBtn)
        reviewBtn   = findViewById(R.id.reviewBtn)
        logText     = findViewById(R.id.logInfoText)
        logTimeText = findViewById(R.id.logTimeText)
        startLogBtn.visibility = View.VISIBLE
        stopLogBtn.visibility = View.GONE
        reviewBtn.visibility = View.GONE
        logText.visibility = View.GONE
        logTimeText.visibility = View.GONE

        // starts logging
        startLogBtn.setOnClickListener{
            startLogging(this)
        }

        // stops logging
        stopLogBtn.setOnClickListener{
            stopLogging(this)
        }

        // Opens LoggedEvent.kt for deciding what to do with the logged events
        reviewBtn.setOnClickListener{
            val openLoading = Intent(applicationContext, LoggedEvent::class.java)
            startActivity(openLoading)
        }
    }

    // ---------------------------------------------------------------------------------------------

    fun startLogging(context: Context){

        isLogging = true

        // Set interface
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

        // Register sensors
        sensorsHandler = SensorsHandler(context)

        // Motion sensors
        if(WatchActivityHandler.getImuStatus()) {
            sensorsHandler.addSensor(SensorType.TYPE_ACCELEROMETER, (1.0/IMUFrequency * 1e6).toInt())
            sensorsHandler.addSensor(SensorType.TYPE_GYROSCOPE, (1.0/IMUFrequency * 1e6).toInt())
            //sensorsHandler.addSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED, "ACC_UNCAL",(1/IMUFrequency * 1e6).toInt())
            //sensorsHandler.addSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, "GYRO_UNCAL", (1/IMUFrequency * 1e6).toInt())
        }
        if(WatchActivityHandler.getImuStatus()) {
            // TODO make specific status for magnetometer
            sensorsHandler.addSensor(SensorType.TYPE_MAGNETIC_FIELD, (1.0/magnetometerFrequency * 1e6).toInt())
            //sensorsHandler.addSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, "MAG_UNCAL", 1000 * 1000)
        }
        if(WatchActivityHandler.getImuStatus()){
            // TODO make specific status for barometer
            sensorsHandler.addSensor(SensorType.TYPE_PRESSURE, (1.0/barometerFrequency * 1e6).toInt())
        }

        // GNSS Sensor
        if(WatchActivityHandler.getGnssStatus()){
            sensorsHandler.addSensor(SensorType.TYPE_GNSS_LOCATION)
            sensorsHandler.addSensor(SensorType.TYPE_GNSS_MEASUREMENTS)
            sensorsHandler.addSensor(SensorType.TYPE_GNSS_MESSAGES)
        }

        // Heart
        sensorsHandler.addSensor(SensorType.TYPE_HEART_RATE, SensorManager.SENSOR_DELAY_FASTEST)
        //sensorsHandler.addSensor(SensorType.TYPE_SPECIFIC_ECG, (1.0/barometerFrequency * 1e6).toInt())

        sensorsHandler.startLogging()
    }

    // ---------------------------------------------------------------------------------------------

    fun stopLogging(context: Context){

        reviewBtn.visibility = View.VISIBLE
        stopLogBtn.visibility = View.GONE
        logTimeText.visibility = View.GONE
        logText.text = "Survey ended"

        isLogging = false
        sensorsHandler.stopLogging()
    }

    // ---------------------------------------------------------------------------------------------

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

    // ---------------------------------------------------------------------------------------------

}