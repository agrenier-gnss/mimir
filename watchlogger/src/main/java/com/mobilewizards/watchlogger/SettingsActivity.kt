package com.mobilewizards.logging_app

import android.annotation.SuppressLint

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.databinding.adapters.SeekBarBindingAdapter.OnProgressChanged
import com.google.android.material.snackbar.Snackbar
import com.mimir.sensors.SensorType
import com.mobilewizards.watchlogger.WatchActivityHandler
import com.mobilewizards.logging_app.databinding.ActivitySettingsBinding

class SettingsActivity : Activity() {
    private lateinit var binding: ActivitySettingsBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Conversion of seekbar progress to frequency
        val progressToFrequency = arrayOf(1, 5, 10, 50, 100, 200)

        // Gathering components
        val sensorsIDs = mapOf(
            SensorType.TYPE_GNSS
                    to mutableListOf(R.id.switch_gnss, 0, R.id.settings_tv_gnss),
            SensorType.TYPE_IMU
                    to mutableListOf(R.id.switch_imu, R.id.settings_sb_imu, R.id.settings_tv_imu),
            SensorType.TYPE_PRESSURE
                    to mutableListOf(R.id.switch_baro, R.id.settings_sb_baro, R.id.settings_tv_baro),
            SensorType.TYPE_STEPS
                    to mutableListOf(R.id.switch_steps, R.id.settings_sb_step, R.id.settings_tv_step),
            SensorType.TYPE_SPECIFIC_ECG
                    to mutableListOf(R.id.switch_ecg, R.id.settings_sb_ecg, R.id.settings_tv_ecg),
            SensorType.TYPE_SPECIFIC_PPG
                    to mutableListOf(R.id.switch_ppg, R.id.settings_sb_ppg, R.id.settings_tv_ppg),
            SensorType.TYPE_SPECIFIC_GSR
                    to mutableListOf(R.id.switch_gsr, R.id.settings_sb_gsr, R.id.settings_tv_gsr),
        )

        val sensorsSwitches = mutableMapOf<SensorType, Switch>()
        val sensorsSeekBar = mutableMapOf<SensorType, SeekBar>()
        val sensorsTextView = mutableMapOf<SensorType, TextView>()

        sensorsIDs.forEach { entry ->
            sensorsSwitches[entry.key] = findViewById<Switch>(entry.value[0])
            sensorsSeekBar[entry.key]  = findViewById<SeekBar>(entry.value[1])
            sensorsTextView[entry.key] = findViewById<TextView>(entry.value[2])
        }

        val btnSave = findViewById<Button>(R.id.button_save)

        // Define a common seekbar listener
        val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Your common implementation for onProgressChanged
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Your common implementation for onStartTrackingTouch
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Your common implementation for onStopTrackingTouch
            }
        }

        // Setting listeners
        sensorsSeekBar.forEach { entry ->
            entry.value.setOnSeekBarChangeListener(seekBarChangeListener)
        }

        // Saving behavior
        btnSave.setOnClickListener{
            sensorsSwitches.forEach{entry ->
                WatchActivityHandler.sensorsSelected[entry.key] = entry.value.isSelected
            }
            sensorsSeekBar.forEach{entry ->
                WatchActivityHandler.sensorsFrequency[entry.key] = progressToFrequency[entry.value.progress]
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