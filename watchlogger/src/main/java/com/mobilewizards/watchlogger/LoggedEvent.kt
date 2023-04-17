package com.mobilewizards.logging_app

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import com.mobilewizards.logging_app.databinding.ActivityLoggedEventBinding

class LoggedEvent : Activity() {

    private lateinit var binding: ActivityLoggedEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoggedEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val logInfoText =  findViewById<TextView>(R.id.logInfotext)

        logInfoText.text = "23.04.23 time 400 logged events from phone"


        //            val openStartSurvey = Intent(applicationContext, LoggingActivity::class.java)
        //            startActivity(openStartSurvey)

    }
}