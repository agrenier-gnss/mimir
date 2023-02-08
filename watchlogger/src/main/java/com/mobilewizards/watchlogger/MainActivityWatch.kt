package com.mobilewizards.watchlogger

import android.app.Activity
import android.os.Bundle
import com.mobilewizards.watchlogger.databinding.ActivityMainWatchBinding

class MainActivityWatch : Activity() {

    private lateinit var binding: ActivityMainWatchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainWatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}