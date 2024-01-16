package com.mobilewizards.logging_app

import android.annotation.SuppressLint


import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
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

        val sensorsSwitches = mapOf<Switch>(
            findViewById<Switch>(R.id.switch_gnss),
            findViewById<Switch>(R.id.switch_imu),
            findViewById<Switch>(R.id.switch_baro),
            findViewById<Switch>(R.id.switch_steps),
            findViewById<Switch>(R.id.switch_ecg),
            findViewById<Switch>(R.id.switch_ppg),
            findViewById<Switch>(R.id.switch_gsr),
        )

        val btnSave = findViewById<Button>(R.id.button_save)

        btnSave.setOnClickListener{
            for(element in sensorsSwitches){

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