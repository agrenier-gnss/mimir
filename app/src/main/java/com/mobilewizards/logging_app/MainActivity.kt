package com.mobilewizards.logging_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

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

        loggingButton.setOnClickListener{
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