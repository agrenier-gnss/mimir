package com.mobilewizards.logging_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.mobilewizards.logging_app.databinding.ActivitySelectionBinding

class SelectionActivity : Activity() {

    private lateinit var binding: ActivitySelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startSurveyBtn = findViewById<Button>(R.id.startSurveyBtn)
        val prevSurveysBtn = findViewById<Button>(R.id.previousSurvBtn)
        val settingsBtn = findViewById<Button>(R.id.settingsBtn)

        startSurveyBtn.setOnClickListener{

            val openStartSurvey = Intent(applicationContext, LoggingActivity::class.java)
            startActivity(openStartSurvey)
        }
        prevSurveysBtn.setOnClickListener{

            /*val openPreviousSurv = Intent(applicationContext, LoggingActivity::class.java)
            startActivity(openStartSurvey)*/
        }
        settingsBtn.setOnClickListener{

            val openSettings = Intent(applicationContext, SettingsActivity::class.java)
            startActivity(openSettings)
        }

    }
}