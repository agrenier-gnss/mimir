package com.mobilewizards.logging_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.mobilewizards.logging_app.databinding.ActivityLogInBinding



class LogInActivity : Activity() {

    private lateinit var binding: ActivityLogInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startBtn = findViewById<Button>(R.id.startBtn)

        startBtn.setOnClickListener{

            val openSelections = Intent(applicationContext, SelectionActivity::class.java)
           startActivity(openSelections)
        }

    }
}