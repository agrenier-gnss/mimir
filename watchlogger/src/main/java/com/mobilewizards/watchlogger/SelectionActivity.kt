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

        startSurveyBtn.setOnClickListener{

            val openStartSurvey = Intent(applicationContext, LoggingActivity::class.java)
            startActivity(openStartSurvey)
        }

    }
}