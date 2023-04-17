package com.mobilewizards.logging_app

import android.app.Activity
import android.os.Bundle
import com.mobilewizards.logging_app.databinding.ActivitySettingsBinding

class SettingsActivity : Activity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}