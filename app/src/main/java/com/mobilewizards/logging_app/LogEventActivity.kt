package com.mobilewizards.logging_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button

class LogEventActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logevent)
        supportActionBar?.hide()


        var isInitialLoad = true

        val footerButton = findViewById<Button>(R.id.footer_button)

        footerButton.setOnClickListener {
            ActivityHandler.toggleButton()
        }

        ActivityHandler.getButtonState().observe(this) { isPressed ->

            footerButton.isSelected = isPressed

            if (isInitialLoad) {
                isInitialLoad = false
                return@observe
            }

            if(isPressed) {

                footerButton.text = "Stop survey"

            } else {

                footerButton.text = "Start survey"

            }

        }

        var x1 = 0f
        var y1 = 0f
        var x2 = 0f
        var y2 = 0f

        findViewById<View>(R.id.activity_log_event_layout).setOnTouchListener { _, touchEvent ->
            when (touchEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    x1 = touchEvent.x
                    y1 = touchEvent.y
                    true
                }
                MotionEvent.ACTION_UP -> {
                    x2 = touchEvent.x
                    y2 = touchEvent.y
                    val deltaX = x2 - x1
                    val deltaY = y2 - y1
                    if (Math.abs(deltaX) > Math.abs(deltaY)) {
                        // swipe horizontal
                        if (Math.abs(deltaX) > 100) {
                            if (deltaX > 0) {
                                // right swipe
                                val intent = Intent(this, MauveActivity::class.java)
                                startActivity(intent)
                                true
                            }
                        }
                    }
                    false
                }
                else -> false
            }
        }
    }
}