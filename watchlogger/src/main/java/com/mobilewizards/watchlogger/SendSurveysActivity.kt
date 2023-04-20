package com.mobilewizards.logging_app

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.core.net.toUri
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import com.mobilewizards.logging_app.databinding.ActivitySendSurveysBinding
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

//Tänne tiedoston lähetys
class SendSurveysActivity : Activity() {

    private lateinit var binding: ActivitySendSurveysBinding
    private val TAG = "watchLogger"
    private val CSV_FILE_CHANNEL_PATH = MediaStore.Downloads.EXTERNAL_CONTENT_URI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySendSurveysBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toPhoneBtn = findViewById<Button>(R.id.SendToPhoneBtn)
        val toDriveBtn = findViewById<Button>(R.id.SendToDrive)


        toPhoneBtn.setOnClickListener{

            sendFiles()


        }

        toDriveBtn.setOnClickListener{

            //
        }

    }

    fun sendFiles(){

        val mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        val sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL)
        for (currentSensor in sensorList) {
            Log.d("List sensors", "Name: ${currentSensor.name} /Type_String: ${currentSensor.stringType} /Type_number: ${currentSensor.type}")
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


}