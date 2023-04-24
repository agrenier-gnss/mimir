package com.mobilewizards.logging_app

import android.annotation.SuppressLint
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {



    private lateinit var mMessageClient: MessageClient
    @SuppressLint("SuspiciousIndentation", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("phoneLogger", "onCreate called")
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        var isInitialLoad = true

        // Check if thread is alive to rightfully enable/disable buttons
        if (counterThread?.isAlive == true) {
            // Implementation of code that require concurrent threads to be running
        }


        val sensorList = arrayOf(
            //1st value: name. second value: is there a slider. third and fourth are min and max values for sliders
            arrayOf("GNSS", false),
            arrayOf("IMU", true, 10, 100),
            arrayOf("Barometer", true, 1, 10),
            arrayOf("Magnetometer", true, 1, 10),
            arrayOf("Bluetooth", false)
        )

        val parentView = findViewById<ViewGroup>(R.id.square_layout)

        //create a layout for each sensor in sensorList
        for(i in sensorList.indices) {

            // Inflate the layout file that contains the TableLayout
            val tableLayout = layoutInflater.inflate(R.layout.layout_presets, parentView, false).findViewById<TableLayout>(R.id.sensorSquarePreset)

            val row = tableLayout.getChildAt(0) as TableRow
            val sensorTitleTextView = row.findViewById<TextView>(R.id.sensorTitle)
            sensorTitleTextView.text = sensorList[i][0].toString()

            var sensorSwitch = row.findViewById<SwitchCompat>(R.id.sensorSwitch)
            sensorSwitch.isChecked = ActivityHandler.getToggle(sensorList[i][0].toString())
            sensorSwitch.isEnabled = !ActivityHandler.getIsLogging() // Disable toggling sensor if logging is ongoing

            var sensorStateTextView = row.findViewById<TextView>(R.id.sensorState)
            setStateTextview(sensorSwitch.isChecked, sensorStateTextView)

            val row2 = tableLayout.getChildAt(1) as TableRow
            val description = row2.findViewById<TextView>(R.id.description)

            sensorSwitch.setOnCheckedChangeListener { _, isChecked ->
                setStateTextview(sensorSwitch.isChecked, sensorStateTextView)
                ActivityHandler.setToggle(sensorList[i][0].toString()) //toggle the status in singleton
            }

            if(sensorList[i][1] == false) {
                // if frequency is can not be changed
                description.text = "${sensorList[i][0]} is always sampled at 1 Hz" // Change the description text
                tableLayout.removeViewAt(2) // Remove the row with the slider.
            } else {
                // if frequency can be changed
                description.text = "Sampling frequency"
                val row3 = tableLayout.getChildAt(2) as TableRow
                val slider = row3.findViewById<SeekBar>(R.id.sensorSlider)
                slider.min = sensorList[i][2].toString().toInt()
                slider.max = sensorList[i][3].toString().toInt()
                slider.progress = ActivityHandler.getFrequency(sensorList[i][0].toString())
                slider.isEnabled = !ActivityHandler.getIsLogging() // Disable changing slider if logging is ongoing

                val sliderValue = row3.findViewById<TextView>(R.id.sliderValue)
                sliderValue.text = ActivityHandler.getFrequency(sensorList[i][0] as String).toString() //set slider value

                slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                        sliderValue.text = progress.toString()
                        ActivityHandler.setFrequency(sensorList[i][0].toString(), progress)
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar) {
                        // Not used
                    }

                    override fun onStopTrackingTouch(seekBar: SeekBar) {
                        // Not used
                    }
                })


            }

            // Remove the tableLayout's parent, if it has one
            (tableLayout.parent as? ViewGroup)?.removeView(tableLayout)

            // Add the TableLayout to the parent view
            parentView.addView(tableLayout)

        }

        var x1 = 0f
        var y1 = 0f
        var x2 = 0f
        var y2 = 0f

        findViewById<View>(R.id.scroll_id).setOnTouchListener { _, touchEvent ->
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
                            // left or right
                            if (deltaX < 0) {
                                // left swipe
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

    private fun sendParameterToWatch(data: DataMap){
        val dataBytes = data.toByteArray()

        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            for (node in nodes) {
                Log.d("data_tag", node.id)
                mMessageClient.sendMessage(node.id, "/message", dataBytes)
                Log.d("data_tag", "msg sent")
            }
        }
    }

    private fun sendTextToWatch(text: String) {
        val dataMap = DataMap().apply {
            putString("data", text)
        }
        val dataByteArray = dataMap.toByteArray()

        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            for (node in nodes) {
                Log.d("data_tag", node.id)
                mMessageClient.sendMessage(node.id, "/message", dataByteArray)
                Log.d("data_tag", "msg sent")
            }
        }
    }
    // Creates main_menu.xml
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.changeParameters -> {
                val setupIntent = Intent(applicationContext, SetupActivity::class.java)
                startActivity(setupIntent)
            }
        }
        return true
    }

    fun setStateTextview(enabled: Boolean,textview: TextView) {
        if (enabled) {
            textview.text = "Enabled"
        } else {
            textview.text = "Disabled"
        }
    }
}