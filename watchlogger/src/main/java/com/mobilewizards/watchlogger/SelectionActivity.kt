package com.mobilewizards.logging_app

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.mobilewizards.logging_app.databinding.ActivitySelectionBinding

class SelectionActivity : Activity() {

    private lateinit var binding: ActivitySelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        this.checkPermissions()

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

    fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.BODY_SENSORS,
        )

        var allPermissionsGranted = true
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false
                break
            }
        }

        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, 225)
        }
    }
}