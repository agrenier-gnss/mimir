package com.mobilewizards.watchlogger

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text
import java.sql.Time
import java.time.LocalDateTime

// TODO: Bad class name, change it. This is not anymore for Heath services, only gets herat rate
class HealthServicesHandler: SensorEventListener{

    private lateinit var mHeartRateSensor: Sensor
    private lateinit var mSensorManager: SensorManager
    private lateinit var context: Context
    private lateinit var text: TextView
    private val heartRateMeasurementList = mutableListOf<String>()
    constructor(context: Context, text: TextView) {
        this.context = context.applicationContext
        this.text = text
    }

    fun getHeartRate() {
        mSensorManager = context.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

        if (mHeartRateSensor == null) {
            Log.d("watchLogger", "heart rate is null")
        } else {
            mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    fun stopHeatRate() {
        mSensorManager.unregisterListener(this)

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "heart-rate_measurements_watch.csv")
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        Log.d("uri", uri.toString())
        uri?.let { mediaUri ->
            context.contentResolver.openOutputStream(mediaUri)?.use { outputStream ->
                outputStream.write("heart_rate,time\n".toByteArray())
                heartRateMeasurementList.forEach { measurementString ->
                    outputStream.write("$measurementString\n".toByteArray())
                }
                outputStream.flush()
            }

            Toast.makeText(context, "Heart rate scan results saved to Downloads folder", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        val measurementsList = mutableListOf<String>()
        for (measurement in sensorEvent?.values!!) {
            val measurement = measurement
            val time = LocalDateTime.now()

            val measurementString =
                "$measurement," +
                "$time"

            measurementsList.add(measurementString)
            Log.d("Heart rate  Measurement", measurementString)
        }

        heartRateMeasurementList.addAll(measurementsList)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }
}