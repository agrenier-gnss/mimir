package com.mobilewizards.logging_app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.mobilewizards.logging_app.databinding.ActivityLoggingBinding

class LoggingActivity : Activity() {

    private lateinit var binding: ActivityLoggingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoggingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startLogBtn = findViewById<Button>(R.id.startLogBtn)
        startLogBtn.visibility = View.VISIBLE

        val stopLogBtn = findViewById<Button>(R.id.stopLogBtn)
        stopLogBtn.visibility = View.GONE

        val reviewBtn = findViewById<Button>(R.id.reviewBtn)
        reviewBtn.visibility = View.GONE

        val logText =  findViewById<TextView>(R.id.logInfoText)
        logText.visibility = View.GONE



        startLogBtn.setOnClickListener{
            startLogBtn.visibility = View.GONE
            stopLogBtn.visibility = View.VISIBLE
            logText.visibility = View.VISIBLE
            logText.text = "Surveying..."


        }
        stopLogBtn.setOnClickListener{

            reviewBtn.visibility = View.VISIBLE
            stopLogBtn.visibility = View.GONE
            logText.text = "Survey ended"
            //logText.visibility = View.GONE
        }

        reviewBtn.setOnClickListener{

            val openLoading = Intent(applicationContext, LoggedEvent::class.java)
            startActivity(openLoading)
        }

    }
}