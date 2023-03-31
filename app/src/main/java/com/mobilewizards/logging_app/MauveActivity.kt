package com.mobilewizards.logging_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MauveActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mauve)

        val loggingButton = findViewById<Button>(R.id.loggingButton)
        loggingButton.setOnClickListener {
            // Handle button click here
        }
    }
}