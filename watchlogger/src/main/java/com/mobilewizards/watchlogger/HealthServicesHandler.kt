package com.mobilewizards.watchlogger

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.mobilewizards.logging_app.BuildConfig
import com.mobilewizards.logging_app.startTime
import org.w3c.dom.Text
import java.io.File
import java.sql.Time
import java.time.LocalDateTime

// TODO: Bad class name, change it. This is not anymore for Heath services, only gets heart rate
class HealthServicesHandler: SensorEventListener{

    private lateinit var mHeartRateSensor: Sensor
    private lateinit var mSensorManager: SensorManager
    private lateinit var context: Context
    private val heartRateMeasurementList = mutableListOf<String>()
    var isLogging = false

    constructor(context: Context) {
        this.context = context.applicationContext
    }

    fun getHeartRate() {
        mSensorManager = context.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)!!

        if (mHeartRateSensor == null) {
            Log.d("ECG", "heart rate is null")
        } else {
            mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }

        isLogging = true
    }

    fun stopHeartRate() {
        mSensorManager.unregisterListener(this)

        isLogging = false

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, "watch_heart_rate_measurements_${
                SimpleDateFormat("ddMMyyyy_hhmmssSSS").format(
                    startTime
                )}.csv")
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { mediaUri ->
            context.contentResolver.openOutputStream(mediaUri)?.use { outputStream ->
                outputStream.write("# ".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write("Header Description:".toByteArray());
                outputStream.write("\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write("Version: ".toByteArray())
                var manufacturer: String = Build.MANUFACTURER
                var model: String = Build.MODEL
                var fileVersion: String = "${BuildConfig.VERSION_CODE}" + " Platform: " +
                        "${Build.VERSION.RELEASE}" + " " + "Manufacturer: "+
                        "${manufacturer}" + " " + "Model: " + "${model}"

                outputStream.write(fileVersion.toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write("\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write("heart_rate,time\n".toByteArray())
                outputStream.write("# ".toByteArray())
                outputStream.write("\n".toByteArray())
                heartRateMeasurementList.forEach { measurementString ->
                    outputStream.write("$measurementString\n".toByteArray())
                }
                outputStream.flush()
            }

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
            uri?.let { getRealPathFromUri(context.contentResolver, it) }
                ?.let {
                    filePath = it}
            WatchActivityHandler.setFilePaths(File(filePath))
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
            Log.d("ECG", measurementString)
        }
        heartRateMeasurementList.addAll(measurementsList)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }
}