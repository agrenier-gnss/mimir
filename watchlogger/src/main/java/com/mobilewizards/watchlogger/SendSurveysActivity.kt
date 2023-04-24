package com.mobilewizards.logging_app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.net.toUri
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import com.mobilewizards.logging_app.databinding.ActivitySendSurveysBinding
import com.mobilewizards.watchlogger.WatchActivityHandler
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

class SendSurveysActivity : Activity() {

    private lateinit var binding: ActivitySendSurveysBinding
    private val TAG = "watchLogger"
    private val CSV_FILE_CHANNEL_PATH = MediaStore.Downloads.EXTERNAL_CONTENT_URI
    private var filePaths = mutableListOf<File>()
    private lateinit var mMessageClient: MessageClient

    private var fileSendOk : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySendSurveysBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val toPhoneBtn = findViewById<Button>(R.id.SendToPhoneBtn)
        val toDriveBtn = findViewById<Button>(R.id.SendToDrive)

        WatchActivityHandler.getFilePaths().forEach{ path ->
            filePaths.add(path)
        }

        toPhoneBtn.setOnClickListener{
            sendFiles()
        }

        toDriveBtn.setOnClickListener{

            //
        }



    }

    private fun fileSendSuccessful(){
        if (!fileSendOk){
            fileSendOk = true
        }
        WatchActivityHandler.fileSendStatus(fileSendOk)

    }

    private fun fileSendTerminated(){
        if (fileSendOk){
            fileSendOk = false
        }

        WatchActivityHandler.fileSendStatus(fileSendOk)

    }

    fun sendFiles(){
        getPhoneNodeId { nodeIds ->
            Log.d(TAG, "Received nodeIds: $nodeIds")
            // Check if there are connected nodes
            var connectedNode: String = if (nodeIds.size > 0) nodeIds[0] else ""

            if (connectedNode.isEmpty()) {
                Log.d(TAG, "no nodes found")
                Toast.makeText(this, "Phone not connected", Toast.LENGTH_SHORT).show()
            } else {
                Log.d(TAG, "nodes found, sending")
                // TODO: Get filepath, and maybe use sendTextToPhone to send the filename/uri to phone

                // Makking fileNames to csv string to send all filenames to phone
                var filenamesToCsv = ""
                WatchActivityHandler.getFilePaths().forEach { file ->
                    filenamesToCsv = filenamesToCsv + file.name + ","
                }
                Log.d(TAG, "filenames csv to send " + filenamesToCsv)
                // Get message client for sending the filename to phone, before file is sent.
                mMessageClient = Wearable.getMessageClient(applicationContext)
                Wearable.getNodeClient(applicationContext).connectedNodes.addOnSuccessListener { nodes ->
                    // making csv string byteArray of the file names
                    val dataMap = DataMap().apply {
                        putString("dataFromWatch", filenamesToCsv)
                    }
                    val dataByteArray = dataMap.toByteArray()

                    // Sending the file name to phone. After that the corresponding file content should be sent
                    // ATM this doesn't work. Code continues even it should only execute after sendMessage is done.
                    mMessageClient.sendMessage(nodes[0].id, "filename", dataByteArray).addOnCompleteListener {
                        Log.d(TAG, "send message complete listener")
                    }.also {


                        // Looping the files to send and calling sendCesFileToPhone
                        // Doesn't get triggered here at all for some reason
                        WatchActivityHandler.getFilePaths().forEach { path ->
                            sendCsvFileToPhone(path, connectedNode, this)
                        }
                    }
                }
                WatchActivityHandler.clearFilfPaths()
            }
        }

        val openSendInfo = Intent(applicationContext, FileSendActivity::class.java)
        startActivity(openSendInfo)
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


    private fun sendCsvFileToPhone(csvFile: File,nodeId: String, context: Context) {
        Log.d(TAG, "in sendCsvFileToPhone")
        // Checks if the file is found and read
        try {
            val bufferedReader = BufferedReader(FileReader(csvFile))
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                line = bufferedReader.readLine()
            }

            bufferedReader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Getting channelClient for sending the file
        val channelClient = Wearable.getChannelClient(context)
        val callback = object : ChannelClient.ChannelCallback() {
            override fun onChannelOpened(channel: ChannelClient.Channel) {
                Log.d(TAG, "onChannelOpened")
                // Send the CSV file to the phone
                Log.d(TAG, "file name " + csvFile.name)
                channelClient.sendFile(channel, csvFile.toUri()).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "inSendFile:" + csvFile.toUri().toString())
                    fileSendSuccessful()
                    channelClient.close(channel)
                } else {
                    Log.e(TAG, "Error with file sending")
                    fileSendTerminated()
                }
            }
        }
            override fun onChannelClosed(
                channel: ChannelClient.Channel,
                closeReason: Int,
                appSpecificErrorCode: Int
            ) {
                Log.d(
                    TAG,
                    "Channel closed: nodeId=$nodeId, reason=$closeReason, errorCode=$appSpecificErrorCode"
                )
                Wearable.getChannelClient(applicationContext).close(channel)
            }
        }

        channelClient.registerChannelCallback(callback)
        channelClient.openChannel(
            nodeId,
            CSV_FILE_CHANNEL_PATH.toString()
        ).addOnCompleteListener { result ->
            Log.d(TAG, result.toString())
            if (result.isSuccessful) {
                Log.d(TAG, "Channel opened: nodeId=$nodeId, path=$CSV_FILE_CHANNEL_PATH")
                callback.onChannelOpened(result.result )
            } else {
                Log.e(
                    TAG,
                    "Failed to open channel: nodeId=$nodeId, path=$CSV_FILE_CHANNEL_PATH"
                )
                channelClient.unregisterChannelCallback(callback)
            }
        }
    }
}