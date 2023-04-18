package com.mobilewizards.logging_app

import android.annotation.SuppressLint


import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.mobilewizards.logging_app.databinding.ActivitySettingsBinding


class SettingsActivity : Activity() {

   // private lateinit var layout: LinearLayout
    private lateinit var binding: ActivitySettingsBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

/*
        layout = findViewById(R.id.activity_settings_layout)

        layout.setOnTouchListener(object : OnSwipeTouchListener(this@SettingsActivity) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                val goBack = Intent(applicationContext, SelectionActivity::class.java)
                startActivity(goBack)
                /*Toast.makeText(this@SettingsActivity, "Swipe Left gesture detected",
                    Toast.LENGTH_SHORT)
                    .show()*/
            }
        })
*/


        val gnssBtn = findViewById<Button>(R.id.GnssBtn)
        val imuBtn = findViewById<Button>(R.id.ImuBtn)
        val ecgBtn = findViewById<Button>(R.id.EcgBtn)

        val fiveSecBtn = findViewById<Button>(R.id.FiveSecondsBtn)
        val fifteenSecBtn = findViewById<Button>(R.id.FifteenSecondsBtn)
        val thirtySecBtn = findViewById<Button>(R.id.ThirtySecondsBtn)
        val minuteBtn = findViewById<Button>(R.id.OneMinuteBtn)


        gnssBtn.setOnClickListener{

            //
        }
        imuBtn.setOnClickListener{

            //
        }
        ecgBtn.setOnClickListener{

            //
        }
        fiveSecBtn.setOnClickListener{

            //
        }
        fifteenSecBtn.setOnClickListener{

            //
        }
        thirtySecBtn.setOnClickListener{

            //
        }
        minuteBtn.setOnClickListener{

            //
        }


    }


}