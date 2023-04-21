package com.mobilewizards.logging_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.mobilewizards.logging_app.LoggingActivity
import com.mobilewizards.logging_app.databinding.ActivityLoggedEventBinding

class LoggedEvent : Activity() {

    private lateinit var binding: ActivityLoggedEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoggedEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val logInfoText =  findViewById<TextView>(R.id.logInfotext)

        //Tähän aito teksti eventistä
        logInfoText.text = "23.04.23 time 400 logged events from phone"



        val loadSurvey = findViewById<ImageButton>(R.id.loadBtn)
        val doNotLoad = findViewById<ImageButton>(R.id.deleteBtn)


        loadSurvey.setOnClickListener{
            val openLoadTo = Intent(applicationContext, SendSurveysActivity::class.java)
            startActivity(openLoadTo)
        }

        doNotLoad.setOnClickListener{

            val goBack = Intent(applicationContext, LoggingActivity::class.java)
            startActivity(goBack)
        }

    }


}