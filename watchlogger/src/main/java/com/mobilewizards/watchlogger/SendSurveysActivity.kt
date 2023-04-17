package com.mobilewizards.logging_app

import android.app.Activity
import android.os.Bundle
import com.mobilewizards.logging_app.databinding.ActivitySendSurveysBinding

class SendSurveysActivity : Activity() {

    private lateinit var binding: ActivitySendSurveysBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySendSurveysBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}