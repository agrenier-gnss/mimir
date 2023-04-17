package com.mobilewizards.logging_app

import android.app.Activity
import android.os.Bundle
import com.mobilewizards.logging_app.databinding.ActivityPrevSurveysBinding

class PrevSurveysActivity : Activity() {

    private lateinit var binding: ActivityPrevSurveysBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPrevSurveysBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}