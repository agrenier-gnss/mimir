package com.mobilewizards.logging_app

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import java.io.File

class SurveyHistoryActivity : AppCompatActivity() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surveyhistory)
        supportActionBar?.hide()

        // Get the parent view to add the TableLayout to
        val parentView = findViewById<ViewGroup>(R.id.container_layout)

        // Path used for searching scan results
        val path = "/storage/emulated/0/Download/"
        val folder = File(path)

        //create a layout for each survey that has been made
        folder.listFiles()?.forEach { file ->

            // Inflate the layout file that contains the TableLayout
            val tableLayout = layoutInflater.inflate(R.layout.layout_presets, parentView, false).findViewById<TableLayout>(R.id.surveySquarePreset)

            // Remove the tableLayout's parent, if it has one
            (tableLayout.parent as? ViewGroup)?.removeView(tableLayout)

            // Set file info into view
            val surveyTime = tableLayout.findViewById<TextView>(R.id.surveyTime)
            surveyTime.text = file.name
            val fileSize = tableLayout.findViewById<TextView>(R.id.fileSize)
            fileSize.text = "${file.length()} bytes"

            // Add the TableLayout to the parent view
            parentView.addView(tableLayout)

            var declineButton: AppCompatImageButton = tableLayout.findViewById(R.id.decline_button)
            declineButton.setOnClickListener {
                try {
                    file.delete()
                }
                catch (e: Exception) {
                    Log.e("Error in deletion of file",e.toString())
                }
            }
        }

        var x1 = 0f
        var y1 = 0f
        var x2 = 0f
        var y2 = 0f

        findViewById<View>(R.id.survey_layout).setOnTouchListener { _, touchEvent ->
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
                            if (deltaX > 0) {
                                // left swipe
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