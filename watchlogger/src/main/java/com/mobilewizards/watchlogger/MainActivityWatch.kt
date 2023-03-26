package com.mobilewizards.logging_app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.mobilewizards.logging_app.databinding.ActivityMainWatchBinding
import com.mobilewizards.watchlogger.HealthServicesHandler
import com.mobilewizards.watchlogger.WatchGNSSHandler
import java.io.File
import java.util.concurrent.ExecutionException


class sMainActivityWatch : Activity() {

    private lateinit var binding: ActivityMainWatchBinding
    private lateinit var mMessageClient: MessageClient
    private lateinit var mChannelClient: ChannelClient

    private val CSV_FILE_CHANNEL_PATH = MediaStore.Downloads.EXTERNAL_CONTENT_URI
    private var barometerFrequency: Int = 1
    private var magnetometerFrequency: Int = 1
    private var IMUFrequency: Int = 10
    private val TAG = "watchLogger"

    private val testFile = File("${CSV_FILE_CHANNEL_PATH}/test")
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMainWatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.checkPermissions()

        var text: TextView = findViewById(R.id.tv_watch)
        text.text = "Text:"

        // Logcat all connected nodes. Phone should show here
        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            Log.d(
                "watchLogger", nodes.toString()
            )
        }
        // Get messages from phone
        mMessageClient = Wearable.getMessageClient(this)
        mMessageClient.addListener {
            Log.d("watchLogger", "it.data " + DataMap.fromByteArray(it.data).toString())
            val dataMap = DataMap.fromByteArray(it.data)
            if(dataMap.containsKey("data")){
                text.text = dataMap.getString("data")
            }
            else if(dataMap.containsKey("barometer")){
                barometerFrequency = dataMap.getInt("barometer")
                text.text = barometerFrequency.toString()
            }
            else if(dataMap.containsKey("magnetometer")){
                magnetometerFrequency = dataMap.getInt("magnetometer")
                text.text = magnetometerFrequency.toString()
            }
            else if(dataMap.containsKey("imu")){
                IMUFrequency = dataMap.getInt("imu")
                text.text = IMUFrequency.toString()
            }
        }
        // File sending


        val Gnss = WatchGNSSHandler(this)
        val healthServices = HealthServicesHandler(this, text)
        var isLogging: Boolean = false
        // Send messages to phone
        val sendButton = findViewById<Button>(R.id.btn_send)
        sendButton.setOnClickListener {
            isLogging = !isLogging
            healthServices.getHeartRate()
            // Commented this out for because testing only heart rate
//            if (isLogging) Gnss.setUpLogging() else Gnss.stopLogging(this)

//            var textToSend = "This is a test text sent from watch"
//            sendTextToWatch(textToSend.toString())
//            Toast.makeText(this, "Text sent", Toast.LENGTH_SHORT).show()

            // Send file to phone
            sendCsvFileToPhone(testFile, getPhoneNodeId()[0], this)
        }
    }

    private fun sendCsvFileToPhone(csvFile: File, nodeId: String, context: Context) {
        val channelClient = Wearable.getChannelClient(context)

        val callback = object : ChannelClient.ChannelCallback() {
            override fun onChannelOpened(channel: ChannelClient.Channel) {
                // Send the CSV file to the phone
                channelClient.sendFile(channel, Uri.fromFile(csvFile)).addOnCompleteListener {
                    // The CSV file has been sent, close the channel
                    channelClient.close(channel)
                }
            }

            override fun onChannelClosed(channel: ChannelClient.Channel, closeReason: Int, appSpecificErrorCode: Int) {
                Log.d(TAG, "Channel closed: nodeId=$nodeId, reason=$closeReason, errorCode=$appSpecificErrorCode")
            }
        }

        channelClient.registerChannelCallback(callback)

        channelClient.openChannel(nodeId,
            CSV_FILE_CHANNEL_PATH.toString()
        ).addOnCompleteListener { result ->
            if (result.isSuccessful) {
                Log.d(TAG, "Channel opened: nodeId=$nodeId, path=$CSV_FILE_CHANNEL_PATH")
            } else {
                Log.e(TAG, "Failed to open channel: nodeId=$nodeId, path=$CSV_FILE_CHANNEL_PATH")
                channelClient.unregisterChannelCallback(callback)
            }
        }
    }

    private fun getPhoneNodeId(): ArrayList<String> {
        var nodeIds =ArrayList<String>()
        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            for (node in nodes) {
                nodeIds.add(node.id.toString())
            }
        }
        return nodeIds
    }

    private fun sendTextToWatch(text: String) {
        val dataMap = DataMap().apply {
           //§ putString("dataFromWatch", text)
        }
        val dataByteArray = dataMap.toByteArray()

        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            for (node in nodes) {
                Log.d("watchLogger", node.id)
                mMessageClient.sendMessage(node.id, "/message", dataByteArray)
                Log.d("watchLogger", "msg sent")
            }
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
