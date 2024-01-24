
package com.mobilewizards.logging_app

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.mimir.sensors.SensorType
import com.mimir.sensors.SensorsHandler
import com.mobilewizards.logging_app.R
import com.mobilewizards.watchlogger.WatchActivityHandler

class LoggingService : Service() {

    private val channelId = "SensorLoggingChannelId"
    private val notificationId = 1
    private lateinit var sensorsHandler : SensorsHandler

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        // Build the notification
        val notification = buildNotification()

        // Start the service in the foreground
        startForeground(notificationId, notification)

        // Start logging
        startLogging(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start your sensor data logging logic here
        // This method will be called when the service is started
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        stopLogging(this)
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            channelId,
            "Sensor Logging Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Sensor Logging Service")
            .setContentText("Logging sensor data...")
            .setSmallIcon(R.drawable.mimirlogo) // Replace with your own icon
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return builder.build()
    }

    // ---------------------------------------------------------------------------------------------

    fun startLogging(context: Context){

        WatchActivityHandler.clearFilfPaths()

        // Register sensors
        sensorsHandler = SensorsHandler(context)


        // GNSS Sensor
        if(WatchActivityHandler.sensorsSelected[SensorType.TYPE_GNSS]?.first as Boolean){
            sensorsHandler.addSensor(SensorType.TYPE_GNSS_LOCATION)
            sensorsHandler.addSensor(SensorType.TYPE_GNSS_MEASUREMENTS)
            sensorsHandler.addSensor(SensorType.TYPE_GNSS_MESSAGES)
        }

        // Motion sensors
        var param = WatchActivityHandler.sensorsSelected[SensorType.TYPE_IMU]
        if(param?.first as Boolean) {
            val frequency = (1.0 / (param.second as Int) * 1e6).toInt()
            // Try to register uncalibrated sensors first, otherwise skip to standard version
            // Accelerometer
            sensorsHandler.addSensor(SensorType.TYPE_ACCELEROMETER_UNCALIBRATED, frequency)
            if(!sensorsHandler.mSensors.last().isRegistered){
                sensorsHandler.addSensor(SensorType.TYPE_ACCELEROMETER, frequency)
            }
            // Gyroscope
            sensorsHandler.addSensor(SensorType.TYPE_GYROSCOPE_UNCALIBRATED, frequency)
            if(!sensorsHandler.mSensors.last().isRegistered){
                sensorsHandler.addSensor(SensorType.TYPE_GYROSCOPE, frequency)
            }
            // Magnetometer
            sensorsHandler.addSensor(SensorType.TYPE_MAGNETIC_FIELD_UNCALIBRATED, frequency)
            if(!sensorsHandler.mSensors.last().isRegistered){
                sensorsHandler.addSensor(SensorType.TYPE_MAGNETIC_FIELD, frequency)
            }
        }

        param = WatchActivityHandler.sensorsSelected[SensorType.TYPE_PRESSURE]
        if(param?.first as Boolean){
            val frequency = (1.0 / (param?.second as Int) * 1e6).toInt()
            sensorsHandler.addSensor(SensorType.TYPE_PRESSURE, frequency)
        }

        param = WatchActivityHandler.sensorsSelected[SensorType.TYPE_STEPS]
        if(param?.first as Boolean){
            val frequency = (1.0 / (param?.second as Int) * 1e6).toInt()
            sensorsHandler.addSensor(SensorType.TYPE_STEP_DETECTOR, frequency)
            sensorsHandler.addSensor(SensorType.TYPE_STEP_COUNTER, frequency)
        }

        // Health sensors
        param = WatchActivityHandler.sensorsSelected[SensorType.TYPE_SPECIFIC_ECG]
        if(param?.first as Boolean){
            val frequency = (1.0 / (param?.second as Int) * 1e6).toInt()
            sensorsHandler.addSensor(SensorType.TYPE_SPECIFIC_ECG, frequency)
        }
        param = WatchActivityHandler.sensorsSelected[SensorType.TYPE_SPECIFIC_PPG]
        if(param?.first as Boolean){
            val frequency = (1.0 / (param?.second as Int) * 1e6).toInt()
            sensorsHandler.addSensor(SensorType.TYPE_SPECIFIC_PPG, frequency)
        }
        param = WatchActivityHandler.sensorsSelected[SensorType.TYPE_SPECIFIC_GSR]
        if(param?.first as Boolean){
            val frequency = (1.0 / (param?.second as Int) * 1e6).toInt()
            sensorsHandler.addSensor(SensorType.TYPE_SPECIFIC_GSR, frequency)
        }

        sensorsHandler.startLogging()
    }

    // ---------------------------------------------------------------------------------------------

    fun stopLogging(context: Context){
        sensorsHandler.stopLogging()
        stopForeground(STOP_FOREGROUND_DETACH)
    }

    // ---------------------------------------------------------------------------------------------
}
