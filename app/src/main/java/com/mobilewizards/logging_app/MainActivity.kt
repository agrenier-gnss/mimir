package com.mobilewizards.logging_app

import android.annotation.SuppressLint
import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.google.android.gms.wearable.*
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.ArrayDeque

data class GnssMeasurement(
    val svId: Int,
    val timeOffsetNanos: Double,
    val state: Int,
    val cn0DbHz: Double,
    val carrierFrequencyHz: Double,
    val pseudorangeRateMeterPerSecond: Double,
    val pseudorangeRateUncertaintyMeterPerSecond: Double
)

class MainActivity : AppCompatActivity() {

    private val CSV_FILE_CHANNEL_PATH = MediaStore.Downloads.EXTERNAL_CONTENT_URI
    val TAG = "tagi"
    private lateinit var mMessageClient: MessageClient
    var filenameStack = ArrayDeque<String>()
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        Log.d("phoneLogger", "onCreate called")
        setContentView(R.layout.activity_main)

        this.checkPermissions()

        val countStartButton = findViewById<Button>(R.id.countStartButton)
        val countStopButton = findViewById<Button>(R.id.countStopButton)

        // Check if thread is alive to rightfully enable/disable buttons
        if (counterThread?.isAlive == true) {
            // Implementation of code that require concurrent threads to be running
        }

        countStartButton.setOnClickListener {
            countStartButton.isEnabled = false
            countStopButton.isEnabled = true
            counterThread = CounterThread()
            counterThread?.start()
        }

        countStopButton.setOnClickListener {
            countStartButton.isEnabled = true
            countStopButton.isEnabled = false
            counterThread?.cancel()
            counterThread = null
        }

        val loggingButton = findViewById<Button>(R.id.startLogButton)
        val stopLogButton = findViewById<Button>(R.id.stopLogButton)
        val motionSensors = MotionSensorsHandler(this)
        val gnss = GnssHandler(this)
        val IMUSlider = findViewById<SeekBar>(R.id.sliderIMU)
        val MagnetometerSlider = findViewById<SeekBar>(R.id.sliderMagnetometer)
        val BarometerSlider = findViewById<SeekBar>(R.id.sliderBarometer)
        val BLE = BLEHandler(this)

        loggingButton.setOnClickListener{
            loggingButton.isEnabled = false
            stopLogButton.isEnabled = true
            Log.d("start logging", "Start logging")
            motionSensors.setUpSensors()
            gnss.setUpLogging()
            BLE.setUpLogging()
        }

        stopLogButton.setOnClickListener {
            loggingButton.isEnabled = true
            stopLogButton.isEnabled = false
            motionSensors.stopLogging()
            gnss.stopLogging(this)
            BLE.stopLogging()
        }

        IMUSlider.min = 10
        IMUSlider.max = 100
        IMUSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("IMUSLIDERPROGRESS", IMUSlider.progress.toString())
                val IMUMap = DataMap().apply{
                    putInt("imu",IMUSlider.progress)
                }
                sendParameterToWatch(IMUMap)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        MagnetometerSlider.min = 1
        MagnetometerSlider.max = 10
        MagnetometerSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("MAGNESLIDERPROGRESS", MagnetometerSlider.progress.toString())
                val magnetometerMap = DataMap().apply{
                    putInt("magnetometer",MagnetometerSlider.progress)
                }
                sendParameterToWatch(magnetometerMap)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        BarometerSlider.min = 1
        BarometerSlider.max = 10
        BarometerSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("BAROSLIDERPROGRESS", BarometerSlider.progress.toString())
                val barometerMap = DataMap().apply{
                    putInt("barometer",BarometerSlider.progress)
                }
                sendParameterToWatch(barometerMap)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        val etTextToWatch: EditText = findViewById(R.id.etTextToWear)
        val sendButton: Button = findViewById(R.id.btnSend)
        val tvTexfFromWatch = findViewById<TextView>(R.id.tv_textFromWatch)

        sendButton.setOnClickListener {
            var textToSend = etTextToWatch.text
            if (textToSend.isEmpty())
                Toast.makeText(this, "Add text", Toast.LENGTH_SHORT).show()
            else
                sendTextToWatch(textToSend.toString())
                Toast.makeText(this, "Text sent", Toast.LENGTH_SHORT).show()
        }

        val channelClient = Wearable.getChannelClient(applicationContext)
        channelClient.registerChannelCallback(object : ChannelClient.ChannelCallback() {
            override fun onChannelOpened(channel: ChannelClient.Channel) {

                val receiveTask = channelClient.receiveFile(channel, ("file:///storage/emulated/0/Download/log_watch_received_${
                    LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS"))}").toUri(), false)
                receiveTask.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("channel", "File successfully stored")
                    } else {
                        Log.e(TAG, "Ei toimi")
                    }
                }
            }
        })
    }



    private fun sendParameterToWatch(data: DataMap){
        val dataBytes = data.toByteArray()

        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            for (node in nodes) {
                Log.d("data_tag", node.id)
                mMessageClient.sendMessage(node.id, "/message", dataBytes)
                Log.d("data_tag", "msg sent")
            }
        }
    }

    private fun sendTextToWatch(text: String) {
        val dataMap = DataMap().apply {
            putString("data", text)
        }
        val dataByteArray = dataMap.toByteArray()

        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            for (node in nodes) {
                Log.d("data_tag", node.id)
                mMessageClient.sendMessage(node.id, "/message", dataByteArray)
                Log.d("data_tag", "msg sent")
            }
        }
    }
    // Creates main_menu.xml
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.changeParameters -> {
                val setupIntent = Intent(applicationContext, SetupActivity::class.java)
                startActivity(setupIntent)
            }
        }
        return true
    }

    fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH_SCAN
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {

            //location permission
            225 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                } else {
                    // Explain to the user that the feature is unavailable
                    AlertDialog.Builder(this)
                        .setTitle("Location permission denied")
                        .setMessage("Permission is denied.")
                        .setPositiveButton("OK",null)
                        .setNegativeButton("Cancel", null)
                        .show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }
}