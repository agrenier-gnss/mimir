package com.mobilewizards.logging_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.mobilewizards.logging_app.databinding.ActivitySettingsBinding

class SettingsActivity : Activity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val gnssBtn = findViewById<Button>(R.id.GnssBtn)
        val imuBtn = findViewById<Button>(R.id.ImuBtn)
        val ecgBtn = findViewById<Button>(R.id.EcgBtn)

        val fiveSecBtn = findViewById<Button>(R.id.FiveSecondsBtn)
        val fifteenSecBtn = findViewById<Button>(R.id.FifteenSecondsBtn)
        val thirtySecBtn = findViewById<Button>(R.id.ThirtySecondsBtn)
        val minuteBtn = findViewById<Button>(R.id.OneMinuteBtn)


        gnssBtn.setOnClickListener{

            //
        }
        imuBtn.setOnClickListener{

            //
        }
        ecgBtn.setOnClickListener{

            //
        }
        fiveSecBtn.setOnClickListener{

            //
        }
        fifteenSecBtn.setOnClickListener{

            //
        }
        thirtySecBtn.setOnClickListener{

            //
        }
        minuteBtn.setOnClickListener{

            //
        }
    }
}