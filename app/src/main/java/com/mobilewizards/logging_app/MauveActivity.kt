package com.mobilewizards.logging_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MauveActivity : AppCompatActivity() {

    private var isToggledOn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mauve)

        val loggingButton = findViewById<Button>(R.id.loggingButton)
        loggingButton.setOnClickListener {
            isToggledOn = !isToggledOn
            if (isToggledOn) {
                findViewById<Button>(R.id.loggingButton).text = "Stop logging"
            } else {
                findViewById<Button>(R.id.loggingButton).text = "Start logging"
            }
        }

        val downloadButton = findViewById<Button>(R.id.downloadDataButton)
        downloadButton.setOnClickListener {

        }
    }
}