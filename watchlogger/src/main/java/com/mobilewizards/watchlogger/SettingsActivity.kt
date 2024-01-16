package com.mobilewizards.logging_app

import android.annotation.SuppressLint


import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import com.mobilewizards.watchlogger.WatchActivityHandler
import com.mobilewizards.logging_app.databinding.ActivitySettingsBinding

class SettingsActivity : Activity() {
    private lateinit var binding: ActivitySettingsBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val gnssBtn = findViewById<Button>(R.id.GnssBtn)
        val imuBtn = findViewById<Button>(R.id.ImuBtn)
        val ecgBtn = findViewById<Button>(R.id.EcgBtn)
        val galBtn = findViewById<Button>(R.id.GalBtn)

        gnssBtn.isSelected = WatchActivityHandler.getGnssStatus()
        imuBtn.isSelected = WatchActivityHandler.getImuStatus()
        ecgBtn.isSelected = WatchActivityHandler.getEcgStatus()
        galBtn.isSelected = WatchActivityHandler.getGalStatus()

        val fiveSecBtn = findViewById<Button>(R.id.FiveSecondsBtn)
        val fifteenSecBtn = findViewById<Button>(R.id.FifteenSecondsBtn)
        val thirtySecBtn = findViewById<Button>(R.id.ThirtySecondsBtn)
        val minuteBtn = findViewById<Button>(R.id.OneMinuteBtn)
        val saveSettingsBtn = findViewById<Button>(R.id.saveSettingsBtn)

        gnssBtn.setOnClickListener{
            gnssBtn.isSelected = !gnssBtn.isSelected
        }

        imuBtn.setOnClickListener{
            imuBtn.isSelected = !imuBtn.isSelected
        }

        ecgBtn.setOnClickListener{
            ecgBtn.isSelected = !ecgBtn.isSelected

            if(ecgBtn.isSelected){
                galBtn.isSelected = false
            }
        }

        galBtn.setOnClickListener {
            galBtn.isSelected = !galBtn.isSelected

            if(galBtn.isSelected){
                ecgBtn.isSelected = false
            }
        }

        fiveSecBtn.setOnClickListener{
            fiveSecBtn.isSelected = !fiveSecBtn.isSelected
            // TODO: Change frequency to correct
            if (fiveSecBtn.isSelected){
                fifteenSecBtn.isSelected = false
                thirtySecBtn.isSelected = false
                minuteBtn.isSelected = false
            }
        }

        fifteenSecBtn.setOnClickListener{
            fifteenSecBtn.isSelected = !fifteenSecBtn.isSelected
            // TODO: Change frequency to correct
            if (fifteenSecBtn.isSelected){
                fiveSecBtn.isSelected = false
                thirtySecBtn.isSelected = false
                minuteBtn.isSelected = false
            }
        }

        thirtySecBtn.setOnClickListener{
            thirtySecBtn.isSelected = !thirtySecBtn.isSelected
            // TODO: Change frequency to correct
            if (thirtySecBtn.isSelected){
                fiveSecBtn.isSelected = false
                fifteenSecBtn.isSelected = false
                minuteBtn.isSelected = false
            }
        }

        minuteBtn.setOnClickListener{
            minuteBtn.isSelected = !minuteBtn.isSelected
            // TODO: Change frequency to correct
            if (minuteBtn.isSelected){
                fiveSecBtn.isSelected = false
                fifteenSecBtn.isSelected = false
                thirtySecBtn.isSelected = false
            }
        }

        saveSettingsBtn.setOnClickListener{
            saveSettingsBtn.isSelected = !saveSettingsBtn.isSelected
            WatchActivityHandler.changeImuStatus(imuBtn.isSelected)
            WatchActivityHandler.changeGnssStatus(gnssBtn.isSelected)
            WatchActivityHandler.changeEcgStatus(ecgBtn.isSelected)
            WatchActivityHandler.changeGalStatus(galBtn.isSelected)

            // TODO: Change frequency to correct
            if (fiveSecBtn.isSelected){
                WatchActivityHandler.changeFrequency(5000)
            }
            else if (fifteenSecBtn.isSelected){
                  WatchActivityHandler.changeFrequency(15000)
            }
            else if (thirtySecBtn.isSelected){
                 WatchActivityHandler.changeFrequency(30000)
            }
            else  if (minuteBtn.isSelected){
                WatchActivityHandler.changeFrequency(60000)
            } else {
                //Default value
                WatchActivityHandler.changeFrequency(1000)
            }
            finish()
        }
    }
}