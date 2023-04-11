package com.mobilewizards.logging_app

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract.Data
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.mobilewizards.logging_app.databinding.ActivityMainWatchBinding
import com.mobilewizards.watchlogger.BLEHandlerWatch
import com.mobilewizards.watchlogger.HealthServicesHandler
import com.mobilewizards.watchlogger.WatchGNSSHandler
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.util.concurrent.ExecutionException


class MainActivityWatch : Activity() {

    private lateinit var binding: ActivityMainWatchBinding
    private lateinit var mMessageClient: MessageClient
    private lateinit var mChannelClient: ChannelClient
    private lateinit var mSensorManager: SensorManager
    private val CSV_FILE_CHANNEL_PATH = MediaStore.Downloads.EXTERNAL_CONTENT_URI
    private var barometerFrequency: Int = 1
    private var magnetometerFrequency: Int = 1
    private var IMUFrequency: Int = 10
    private val TAG = "watchLogger"

    private val testFile = "test_file"
    private val testDataToFile = listOf<Int>(1,2,3,4,5,6)
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityMainWatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.checkPermissions()
        var text: TextView = findViewById(R.id.tv_watch)
        text.text = "Text:"

        val ble =  BLEHandlerWatch(this)
        val gnss = WatchGNSSHandler(this)
        val healthServices = HealthServicesHandler(this, text)
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
        var isLogging: Boolean = false
        val sendButton = findViewById<Button>(R.id.btn_send)
        sendButton.setOnClickListener {
            isLogging = !isLogging
            val mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

            val sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL)
            for (currentSensor in sensorList) {
                Log.d("List sensors", "Name: ${currentSensor.name} /Type_String: ${currentSensor.stringType} /Type_number: ${currentSensor.type}")
            }
//            healthServices.getHeartRate()// For testing is here. (un)comment if needed or not
//            gnss.setUpLogging() // For testing is here. (un)comment if needed or not
//            ble.setUpLogging()// For testing is here. (un)comment if needed or not

            // Commented this out for because testing only heart rate
//            if (isLogging) Gnss.setUpLogging() else Gnss.stopLogging(this)

//            var textToSend = "This is a test text sent from watch"
//            sendTextToWatch(textToSend.toString())
//            Toast.makeText(this, "Text sent", Toast.LENGTH_SHORT).show()

            // Send file to phone
//            Log.d(TAG, "in button press, nodeId size " + getPhoneNodeId().size.toString())
            // TODO: Add no connected node handler
            getPhoneNodeId { nodeIds ->
                Log.d(TAG, "Received nodeIds: $nodeIds")

                var connectedNode: String = if (nodeIds.size > 0) nodeIds[0] else ""

                if (connectedNode.isEmpty()) {
                    Log.d(TAG, "no nodes found")
                } else {
                    Log.d(TAG, "nodes found, sending")
                    sendCsvFileToPhone(File("/storage/emulated/0/Download/test_file.csv"), connectedNode, applicationContext)
                }
            }
        }
    }

    private fun sendCsvFileToPhone(csvFile: File, nodeId: String, context: Context) {

        // Check if the file is found and read
        try {
            val bufferedReader = BufferedReader(FileReader(csvFile))

            var line: String? = bufferedReader.readLine()

            while (line != null) {
                Log.d(TAG, line)
                line = bufferedReader.readLine()
            }

            bufferedReader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val channelClient = Wearable.getChannelClient(context)

        val callback = object : ChannelClient.ChannelCallback() {
            override fun onChannelOpened(channel: ChannelClient.Channel) {
                Log.d(TAG, "onChannelOpened")
                // Send the CSV file to the phone
                channelClient.sendFile(channel, csvFile.toUri()).addOnCompleteListener {task ->
                    // The CSV file has been sent, close the channel
                    if (task.isSuccessful) {
                        Log.d(TAG, "inSendFile:" + csvFile.toUri().toString())
                        channelClient.close(channel)
                    } else {
                        Log.e(TAG, "Error with file sending")
                    }

                }
            }

            override fun onChannelClosed(channel: ChannelClient.Channel, closeReason: Int, appSpecificErrorCode: Int) {
                Log.d(TAG, "Channel closed: nodeId=$nodeId, reason=$closeReason, errorCode=$appSpecificErrorCode")
                Wearable.getChannelClient(applicationContext).close(channel)
            }
        }

        channelClient.registerChannelCallback(callback)

        channelClient.openChannel(nodeId,
            CSV_FILE_CHANNEL_PATH.toString()
        ).addOnCompleteListener { result ->
            Log.d(TAG, result.toString())
            if (result.isSuccessful) {
                Log.d(TAG, "Channel opened: nodeId=$nodeId, path=$CSV_FILE_CHANNEL_PATH")
                callback.onChannelOpened(result.result)
            } else {
                Log.e(TAG, "Failed to open channel: nodeId=$nodeId, path=$CSV_FILE_CHANNEL_PATH")
                channelClient.unregisterChannelCallback(callback)
            }
        }
    }

    private fun getPhoneNodeId(callback: (ArrayList<String>) -> Unit) {
        var nodeIds = ArrayList<String>()
        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            for (node in nodes) {
                Log.d(TAG, "connected node in getPhoneId " + node.id.toString())
                nodeIds.add(node.id.toString())
            }
            Log.d(TAG, "in getPhonenodeids size " + nodeIds.size.toString())
            callback(nodeIds)
        }
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
