package com.mobilewizards.logging_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable

class MainActivity : AppCompatActivity() {

    private lateinit var mMessageClient: MessageClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val countStartButton = findViewById<Button>(R.id.countStartButton)
        val countStopButton = findViewById<Button>(R.id.countStopButton)

        // Check if thread is alive to rightfully enable/disable buttons
        if (counterThread?.isAlive == true) {
            countStartButton.isEnabled = false
            countStopButton.isEnabled = true
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

        // Check if thread is alive to rightfully enable/disable buttons
        if (motionSensors.listenerActive) {
            loggingButton.isEnabled = false
            stopLogButton.isEnabled = true
        }

        loggingButton.setOnClickListener {
            loggingButton.isEnabled = false
            stopLogButton.isEnabled = true
            Log.d("start logging", "Start logging")
            motionSensors.setUpSensors()
        }

        stopLogButton.setOnClickListener {
            loggingButton.isEnabled = true
            stopLogButton.isEnabled = false
            motionSensors.stopLogging()
        }


        val etTextToWatch: EditText = findViewById(R.id.etTextToWear)
        val sendButton: Button = findViewById(R.id.btnSend)

        mMessageClient = Wearable.getMessageClient(this)
        sendButton.setOnClickListener {
            var textToSend = etTextToWatch.text
            if (textToSend.isEmpty())
                Toast.makeText(this, "Add text", Toast.LENGTH_SHORT).show()
            else
                sendTextToWatch(textToSend.toString())
        }
    }



    private fun sendTextToWatch(text: String) {
        val dataMap = DataMap().apply {
            putString("data", text)
        }

        var nodeId = ""
        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            for (node in nodes) {
                nodeId = node.id
            }
        }

        val dataByteArray = dataMap.toByteArray()

        mMessageClient.sendMessage(nodeId, "/data", dataByteArray)
    }
    // Creates main_menu.xml
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.changeParameters -> {
                val setupIntent = Intent(this, SetupActivity::class.java)
                startActivity(setupIntent)
            }
        }
        return true
    }



}