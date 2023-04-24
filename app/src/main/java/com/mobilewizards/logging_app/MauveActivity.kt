package com.mobilewizards.logging_app

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.*

class MauveActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()

        val loggingButton = findViewById<Button>(R.id.loggingButton)
        val dataButton = findViewById<Button>(R.id.downloadDataButton)
        val loggingText = findViewById<TextView>(R.id.loggingTextView)
        val timeText = findViewById<TextView>(R.id.loggingTimeTextView)

        // Prevent logging button from going to unintended locations
        if(ActivityHandler.getIsLogging()) {

            dataButton.visibility = View.GONE
            loggingButton.text = "Stop logging"

            loggingButton.translationY = 250f

            Handler().postDelayed({
                loggingText.text = "Surveying..."
                timeText.text = "Started ${ActivityHandler.getSurveyStartTime()}"
            }, 300)
        } else {

            val layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID

            loggingButton.layoutParams = layoutParams
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mauve)
        supportActionBar?.hide()

        this.checkPermissions()

        val motionSensors = MotionSensorsHandler(this)
        val gnss = GnssHandler(this)
        val BLE = BLEHandler(this)


        var isInitialLoad = true

        val loggingButton = findViewById<Button>(R.id.loggingButton)
        val dataButton = findViewById<Button>(R.id.downloadDataButton)
        val loggingText = findViewById<TextView>(R.id.loggingTextView)
        val timeText = findViewById<TextView>(R.id.loggingTimeTextView)

        //if logging button is toggled in other activities, it is also toggled in here.

        loggingButton.setOnClickListener {
            ActivityHandler.toggleButton(this)
        }

        dataButton.setOnClickListener {
            val intent = Intent(this, SurveyHistoryActivity::class.java)
            startActivity(intent)
        }

        ActivityHandler.getButtonState().observe(this) { isPressed ->
            loggingButton.isSelected = isPressed

            // Check if app has just started and skip toggled off code
            if (isInitialLoad) {
                isInitialLoad = false
                return@observe
            }

            if(isPressed) {
                // Start logging
                findViewById<Button>(R.id.loggingButton).text = "Stop logging"
                dataButton.visibility = View.GONE
                loggingButton.animate()
                    .translationYBy(250f)
                    .setDuration(500)
                    .start()

                Handler().postDelayed({
                    loggingText.text = "Surveying..."
                    timeText.text = "Started ${ActivityHandler.getSurveyStartTime()}"
                }, 300)

            } else {
                // Stop logging
                findViewById<Button>(R.id.loggingButton).text = "Start logging"
                loggingText.text = ""
                timeText.text = ""
                loggingButton.animate()
                    .translationYBy(-250f)
                    .setDuration(200)
                    .start()

                Handler().postDelayed({
                    dataButton.visibility = View.VISIBLE
                }, 100)
            }
        }

        //change to another activity by sweeping
        var x1 = 0f
        var y1 = 0f
        var x2 = 0f
        var y2 = 0f

        findViewById<View>(R.id.activity_mauve_layout).setOnTouchListener { _, touchEvent ->
            when (touchEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    x1 = touchEvent.x
                    y1 = touchEvent.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    x2 = touchEvent.x
                    y2 = touchEvent.y
                    val deltaX = x2 - x1
                    val deltaY = y2 - y1
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        // swipe horizontal
                        if (Math.abs(deltaX) > 100) {
                            // left or right
                            if (deltaX < 0) {
                                // left swipe
                                val intent = Intent(this, LogEventActivity::class.java)
                                startActivity(intent)
                                true
                            } else if (deltaX > 0) {
                                // right swipe
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                true
                            }
                        }
                    }
                    // add a default return value of false here
                    false
                }
                else -> false
            }
        }
    }

    private fun checkPermissions() {
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