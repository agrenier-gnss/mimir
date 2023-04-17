package com.mobilewizards.logging_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.os.Handler
import android.os.Looper
import com.mobilewizards.logging_app.databinding.ActivityLogInBinding


@Suppress("DEPRECATION")
class LogInActivity : Activity() {

    private lateinit var binding: ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)


        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SelectionActivity::class.java)
            startActivity(intent)
            finish()
        }, 4000)


    }
}