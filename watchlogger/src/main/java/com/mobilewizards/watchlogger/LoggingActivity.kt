package com.mobilewizards.logging_app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import com.mobilewizards.logging_app.databinding.ActivityLoggingBinding

class LoggingActivity : Activity() {

    private lateinit var binding: ActivityLoggingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoggingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startLogBtn = findViewById<Button>(R.id.startLogBtn)
        var isLogging = false

        startLogBtn.setOnClickListener{

            if(!isLogging){
                isLogging = true
                startLogBtn.text = "Stop"

                //Näihin oikeat värit
                startLogBtn.setBackgroundColor(Color.BLUE);
                startLogBtn.setTextColor(Color.WHITE);

            } else {

                isLogging = false
                startLogBtn.text = "Start"

                //Näihin oikeat värit
                startLogBtn.setBackgroundColor(Color.WHITE);
                startLogBtn.setTextColor(Color.BLACK);

            }

        }

    }
}