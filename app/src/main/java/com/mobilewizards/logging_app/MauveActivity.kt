package com.mobilewizards.logging_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView

class MauveActivity : AppCompatActivity() {

    private var isToggledOn: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mauve)
        supportActionBar?.hide()

        val loggingButton = findViewById<Button>(R.id.loggingButton)
        val dataButton = findViewById<Button>(R.id.downloadDataButton)
        val loggingText = findViewById<TextView>(R.id.loggingTextView)

        loggingButton.setOnClickListener {
            if (isToggledOn) {
                findViewById<Button>(R.id.loggingButton).text = "Start logging"

                loggingText.text = ""

                loggingButton.animate()
                    .translationYBy(-250f)
                    .setDuration(200)
                    .start()

                Handler().postDelayed({
                    dataButton.visibility = View.VISIBLE
                }, 100)

            } else {
                findViewById<Button>(R.id.loggingButton).text = "Stop logging"

                dataButton.visibility = View.GONE

                loggingButton.animate()
                    .translationYBy(250f)
                    .setDuration(500)
                    .start()

                Handler().postDelayed({
                    loggingText.text = "Placeholder text ..."
                }, 300)

            }
            isToggledOn = !isToggledOn
        }

        val downloadButton = findViewById<Button>(R.id.downloadDataButton)
        downloadButton.setOnClickListener {

        }
    }
}