package com.mobilewizards.logging_app

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import com.mobilewizards.logging_app.databinding.ActivityMainWatchBinding
import com.mobilewizards.watchlogger.HealthServicesHandler
import com.mobilewizards.watchlogger.WatchGNSSHandler


class MainActivityWatch : Activity() {

    private lateinit var binding: ActivityMainWatchBinding
    private lateinit var mMessageClient: MessageClient


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
            text.text = dataMap.getString("data")
        }


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
        }





    }
    private fun sendTextToWatch(text: String) {
        val dataMap = DataMap().apply {
            putString("dataFromWatch", text)
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
