package com.mobilewizards.logging_app

import android.annotation.SuppressLint


import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
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

        gnssBtn.isSelected = WatchActivityHandler.getGnssStatus()
        imuBtn.isSelected = WatchActivityHandler.getImuStatus()
        ecgBtn.isSelected = WatchActivityHandler.getEcgStatus()


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
        }

        fiveSecBtn.setOnClickListener{
            fiveSecBtn.isSelected = !fiveSecBtn.isSelected
            // TODO: Chagne frequency to correct
            if (fiveSecBtn.isSelected){
               // changeFrequencyStatus(fiveSecBtn)
                fifteenSecBtn.isSelected = false
                thirtySecBtn.isSelected = false
                minuteBtn.isSelected = false
               // WatchActivityHandler.changeFrequency(5)
            }
        }

        fifteenSecBtn.setOnClickListener{
            fifteenSecBtn.isSelected = !fifteenSecBtn.isSelected
            // TODO: Chagne frequency to correct
            if (fifteenSecBtn.isSelected){
                fiveSecBtn.isSelected = false
                thirtySecBtn.isSelected = false
                minuteBtn.isSelected = false
              //  WatchActivityHandler.changeFrequency(15)
            }
        }

        thirtySecBtn.setOnClickListener{
            thirtySecBtn.isSelected = !thirtySecBtn.isSelected
            // TODO: Chagne frequency to correct
            if (thirtySecBtn.isSelected){
                fiveSecBtn.isSelected = false
                fifteenSecBtn.isSelected = false
                minuteBtn.isSelected = false
               // WatchActivityHandler.changeFrequency(30)
            }
        }

        minuteBtn.setOnClickListener{
            minuteBtn.isSelected = !minuteBtn.isSelected
            // TODO: Chagne frequency to correct
            if (minuteBtn.isSelected){
                fiveSecBtn.isSelected = false
                fifteenSecBtn.isSelected = false
                thirtySecBtn.isSelected = false
                //WatchActivityHandler.changeFrequency(60)
            }
        }

        saveSettingsBtn.setOnClickListener{
            saveSettingsBtn.isSelected = !saveSettingsBtn.isSelected
            WatchActivityHandler.changeImuStatus(imuBtn.isSelected)
            WatchActivityHandler.changeGnssStatus(gnssBtn.isSelected)
            WatchActivityHandler.changeEcgStatus(ecgBtn.isSelected)

            // TODO: Chagne frequency to correct
            if (fiveSecBtn.isSelected){
                WatchActivityHandler.changeFrequency(5)
            }
            else if (fifteenSecBtn.isSelected){
                  WatchActivityHandler.changeFrequency(15)
            }
            else if (thirtySecBtn.isSelected){
                 WatchActivityHandler.changeFrequency(30)
            }
            else  if (minuteBtn.isSelected){
                WatchActivityHandler.changeFrequency(60)
            } else {
                //Default value
                WatchActivityHandler.changeFrequency(5)
            }
        }
    }
}