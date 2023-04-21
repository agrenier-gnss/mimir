package com.mobilewizards.logging_app

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.MessageClient
import com.mobilewizards.logging_app.databinding.ActivityLoggingBinding
import com.mobilewizards.logging_app.databinding.ActivityMainWatchBinding
import com.mobilewizards.watchlogger.BLEHandlerWatch
import com.mobilewizards.watchlogger.HealthServicesHandler
import com.mobilewizards.watchlogger.WatchGNSSHandler
import com.mobilewizards.watchlogger.WatchActivityHandler
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LoggingActivity : Activity() {

    private lateinit var binding: ActivityLoggingBinding

    private lateinit var mMessageClient: MessageClient
    private lateinit var mChannelClient: ChannelClient
    private lateinit var mSensorManager: SensorManager
    private val CSV_FILE_CHANNEL_PATH = MediaStore.Downloads.EXTERNAL_CONTENT_URI
    private var barometerFrequency: Int = 1
    private var magnetometerFrequency: Int = 1
    private var IMUFrequency: Int = 10
    private val TAG = "watchLogger"

    private val testFile = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"))
    private val testDataToFile = listOf<Int>(1,2,3,4,5,6)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoggingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        this.checkPermissions()

        val ble =  BLEHandlerWatch(this)
        val gnss = WatchGNSSHandler(this)

        //Constructor huono, kellossa ei näytetä sykettä käyttäjälle, ei tarvita text?
        //val healthServices = HealthServicesHandler(this, text)

        //val loggedEvent = LoggedEvent()


        // writing into test file
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, testFile)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = this.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        Log.d("uri", uri.toString())
        uri?.let { mediaUri ->
            this.contentResolver.openOutputStream(mediaUri)?.use { outputStream ->
                outputStream.write("SvId,Time offset in nanos,State,cn0DbHz,carrierFrequencyHz,pseudorangeRateMeterPerSecond,pseudorangeRateUncertaintyMeterPerSecond\n".toByteArray())
                testDataToFile.forEach { measurementString ->
                    outputStream.write("$measurementString\n".toByteArray())

                }
                outputStream.flush()
            }
        }


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
        uri?.let { getRealPathFromUri(applicationContext.contentResolver, it) }
            ?.let { Log.d("uri", it)
                filePath = it}



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



        //When clicked, starts logging
        startLogBtn.setOnClickListener{

            val currentTime = LocalDateTime.now()

            startLogBtn.visibility = View.GONE
            stopLogBtn.visibility = View.VISIBLE
            logText.visibility = View.VISIBLE
            logText.text = "Surveying..."
            logTimeText.visibility = View.VISIBLE
            logTimeText.text = currentTime.toString()

            //tähän tarkistus, mitkä logattavat arvot valittu
            //healthServices.getHeartRate()
            gnss.setUpLogging()
            ble.setUpLogging()

        }

        //When clicked, stops logging
        stopLogBtn.setOnClickListener{

            reviewBtn.visibility = View.VISIBLE
            stopLogBtn.visibility = View.GONE
            logTimeText.visibility = View.GONE
            logText.text = "Survey ended"
            gnss.stopLogging(this)
            ble.stopLogging()

            //logText.visibility = View.GONE
        }

        //When clicked, opens LoggedEvent.kt for deciding what to do with the logged events
        reviewBtn.setOnClickListener{

           WatchActivityHandler.getFilePath(filePath)
            val openLoading = Intent(applicationContext, LoggedEvent::class.java)
            startActivity(openLoading)
        }

    }

    //TÄMÄ EI VIELÄ NÄY KÄYTTÄJÄLLE
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