package com.mobilewizards.logging_app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.checkLocationPermissions()

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
        val Gnss = GnssHandler(this)

        loggingButton.setOnClickListener{
            loggingButton.isEnabled = false
            stopLogButton.isEnabled = true
            Log.d("start logging", "Start logging")
            motionSensors.setUpSensors()
            Gnss.setUpLogging()
        }

        stopLogButton.setOnClickListener {
            loggingButton.isEnabled = true
            stopLogButton.isEnabled = false
            motionSensors.stopLogging()
            Gnss.stopLogging()
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
    fun checkLocationPermissions(){

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ){

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION),225 );

        }



    }
}