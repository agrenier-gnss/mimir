package com.mobilewizards.logging_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*

class LogEventActivity : AppCompatActivity() {

    val timer = Timer()

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logevent)
        supportActionBar?.hide()


        val parentView = findViewById<LinearLayout>(R.id.data_layout)


        // List of sensors to be logged. Tag only
        val activityList = arrayOf("GNSS", "IMU", "Magnetometer", "Barometer", "Bluetooth")
        var activeSensorList = mutableListOf<String>()

        //create a layout for each activity in activityList
        for (i in activityList.indices) {

            val layout = layoutInflater.inflate(R.layout.layout_presets, parentView, false)
                .findViewById<LinearLayout>(R.id.logEventSquarePreset)
            val datapoint = layout.findViewById<TextView>(R.id.logEventDataPoint)
            val activityTitleTextView = layout.findViewById<TextView>(R.id.logEventTitle)
            val description = layout.findViewById<TextView>(R.id.logEventDescription)

            // Inflate the layout file that contains the gridview

            val frequency = ActivityHandler.getFrequency(activityList[i])
            activityTitleTextView.text = activityList[i]

            if (!ActivityHandler.getToggle(activityList[i])) {
                description.text = "${activityList[i]} is disabled"
                datapoint.text = "Data not available"
            } else {
                activeSensorList.add(activityList[i])
                if (frequency == 0) {
                    if (activityList[i] == "Time") {
                        description.text = "Survey duration"
                    } else {
                        description.text = "1 hz frequency"
                    }
                } else {
                    description.text = "$frequency hz frequency"
                }
                datapoint.text = ActivityHandler.getLogData(activityList[i]).toString()
            }

            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    // This code will run every second
                    if (activityList[i] == "Magnetometer" && ActivityHandler.isLogging()) {
                        datapoint.text = ActivityHandler.imuSensor[0].getMagnetometerValues().size.toString()
                    }
                    if(activityList[i] == "Bluetooth" && ActivityHandler.isLogging()) {
                        //todo: not logging anything
                        datapoint.text = ActivityHandler.bleSensor[0].getBLEValues().size.toString()
                    }
                    if(activityList[i] == "GNSS" && ActivityHandler.isLogging()) {
                        datapoint.text = ActivityHandler.gnssSensor[0].getGNSSValues().size.toString()
                    }
                    if(activityList[i] == "Barometer" && ActivityHandler.isLogging()) {
                        datapoint.text = ActivityHandler.imuSensor[0].getBarometerValues().size.toString()
                    }
                    if(activityList[i] == "IMU" && ActivityHandler.isLogging()) {
                        var sum = 0
                        for(j in ActivityHandler.imuSensor[0].getIMUValues()) {
                            sum += j.size
                        }
                        datapoint.text = sum.toString()
                    }
                }
            }, 0, 1000)


            // Remove the tableLayout's parent, if it has one
            (layout.parent as? ViewGroup)?.removeView(layout)

            // Add the TableLayout to the parent view
            parentView.addView(layout)
        }

        // Switch views by swiping
        var x1 = 0f
        var y1 = 0f
        var x2 = 0f
        var y2 = 0f
        findViewById<View>(R.id.activity_log_event_layout).setOnTouchListener { _, touchEvent ->
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
                            if (deltaX > 0) {
                                // right swipe
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                true
                            }
                        }
                    }
                    false
                }
                else -> false
            }
        }
    }
}