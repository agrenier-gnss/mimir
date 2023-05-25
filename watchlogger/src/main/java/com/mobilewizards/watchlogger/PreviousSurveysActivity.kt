package com.mobilewizards.logging_app

import android.app.Activity
import android.os.Bundle
import com.mobilewizards.logging_app.databinding.ActivityPreviousSurveysBinding

class PreviousSurveysActivity : Activity() {

    // TODO: Create option to browse and select a certain survey to send to phone

    private lateinit var binding: ActivityPreviousSurveysBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreviousSurveysBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}