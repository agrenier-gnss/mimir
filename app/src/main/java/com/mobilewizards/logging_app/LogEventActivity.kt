package com.mobilewizards.logging_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*

class LogEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logevent)
        supportActionBar?.hide()


        val parentView = findViewById<LinearLayout>(R.id.data_layout)


        // List of sensors to be logged. Tag only
        val activityList = arrayOf(
            // 1st value: name. 2nd value: description text
            arrayOf("Time"),
            arrayOf("GNSS"),
            arrayOf("IMU"),
            arrayOf("Magnetometer"),
            arrayOf("Barometer")
        )

        //create a layout for each activity in activityList
        for(i in activityList.indices) {

            // Inflate the layout file that contains the gridview
            val layout = layoutInflater.inflate(R.layout.layout_presets, parentView, false).findViewById<LinearLayout>(R.id.logEventSquarePreset)

            val activityTitleTextView = layout.findViewById<TextView>(R.id.logEventTitle)
            activityTitleTextView.text = activityList[i][0].toString()

            val description = layout.findViewById<TextView>(R.id.logEventDescription)
            val frequency = ActivityHandler.getFrequency(activityList[i][0])

            val datapoint = layout.findViewById<TextView>(R.id.logEventDataPoint)

            if(!ActivityHandler.getToggle(activityList[i][0])) {
                description.text = "${activityList[i][0]} disabled"
                datapoint.text = "Error"
            } else {
                if(frequency == 0) {
                    if(activityList[i][0] == "Time") {
                        description.text = "Survey duration"
                    } else {
                        description.text = "1 hz frequency"
                    }
                } else {
                    description.text = "$frequency hz frequency"
                }
                datapoint.text = ActivityHandler.getLogData(activityList[i][0]).toString()
            }





            // Remove the tableLayout's parent, if it has one
            (layout.parent as? ViewGroup)?.removeView(layout)

            // Add the TableLayout to the parent view
            parentView.addView(layout)

        }

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
                                val intent = Intent(this, MauveActivity::class.java)
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