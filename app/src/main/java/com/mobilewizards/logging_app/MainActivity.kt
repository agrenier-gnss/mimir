package com.mobilewizards.logging_app

import android.annotation.SuppressLint
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {

    private lateinit var mMessageClient: MessageClient
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("phoneLogger", "onCreate called")
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        this.checkPermissions()



        // Check if thread is alive to rightfully enable/disable buttons
        if (counterThread?.isAlive == true) {
            // Implementation of code that require concurrent threads to be running
        }




        var gnssToggle = true
        var gyroscopeToggle = true
        var accelerometerToggle = true
        var magnetometerToggle = true
        var barometerToggle = true

        val gnssStateTextView = findViewById<TextView>(R.id.gnssState)




        val gyroscopeStateTextView = findViewById<TextView>(R.id.gyroState)
        val gyroscopeSliderTextView = findViewById<TextView>(R.id.gyroValue)
        val gyroscopeSlider = findViewById<SeekBar>(R.id.sliderGyroscope)
        gyroscopeSlider.min = 10
        gyroscopeSlider.max = 100
        gyroscopeSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("GYROSCOPELIDERPROGRESS", gyroscopeSlider.progress.toString())
                val accelerometerMap = DataMap().apply{
                    putInt("accelerometer",gyroscopeSlider.progress)
                }
                gyroscopeSliderTextView.setText(gyroscopeSlider.progress.toString()+"hz")
                //sendParameterToWatch(accelerometerMap)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        gyroscopeSliderTextView.setText(gyroscopeSlider.progress.toString()+"hz")

        val barometerStateTextView = findViewById<TextView>(R.id.baroState)
        val barometerSliderTextView = findViewById<TextView>(R.id.baroValue)
        val BarometerSlider = findViewById<SeekBar>(R.id.sliderBarometer)
        BarometerSlider.min = 1
        BarometerSlider.max = 10
        BarometerSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("BAROSLIDERPROGRESS", BarometerSlider.progress.toString())
                val barometerMap = DataMap().apply{
                    putInt("barometer",BarometerSlider.progress)
                }
                barometerSliderTextView.setText(BarometerSlider.progress.toString()+"hz")
                //sendParameterToWatch(barometerMap)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        barometerSliderTextView.setText(BarometerSlider.progress.toString()+"hz")


        val magnetometerStateTextView = findViewById<TextView>(R.id.magnetoState)
        val magnetometerSliderTextView = findViewById<TextView>(R.id.magnetoValue)
        val MagnetometerSlider = findViewById<SeekBar>(R.id.sliderMagnetometer)
        MagnetometerSlider.min = 1
        MagnetometerSlider.max = 10
        MagnetometerSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("MAGNESLIDERPROGRESS", MagnetometerSlider.progress.toString())
                val magnetometerMap = DataMap().apply{
                    putInt("magnetometer",MagnetometerSlider.progress)
                }
                magnetometerSliderTextView.setText(MagnetometerSlider.progress.toString()+"hz")
                //sendParameterToWatch(magnetometerMap)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        magnetometerSliderTextView.setText(MagnetometerSlider.progress.toString()+"hz")


        val accelerometerStateTextView = findViewById<TextView>(R.id.acceleroState)
        val accelerometerSliderTextView = findViewById<TextView>(R.id.acceleroValue)
        val accelerometerSlider = findViewById<SeekBar>(R.id.sliderAccelerometer)
        accelerometerSlider.min = 10
        accelerometerSlider.max = 100
        accelerometerSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("IMUSLIDERPROGRESS", accelerometerSlider.progress.toString())
                val accelerometerMap = DataMap().apply{
                    putInt("accelerometer",accelerometerSlider.progress)
                }
                accelerometerSliderTextView.setText(accelerometerSlider.progress.toString()+"hz")
                //sendParameterToWatch(accelerometerMap)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        accelerometerSliderTextView.setText(accelerometerSlider.progress.toString()+"hz")

        val loggingButton = findViewById<Button>(R.id.startLogButton)
        val motionSensors = MotionSensorsHandler(this)
        val gnss = GnssHandler(this)




        val BLE = BLEHandler(this)







        loggingButton.setOnClickListener{
            loggingButton.isEnabled = false
            //stopLogButton.isEnabled = true
            MagnetometerSlider.isEnabled = false
            BarometerSlider.isEnabled = false
            Log.d("start logging", "Start logging")
            motionSensors.setUpSensors()
            if (gnssToggle) {gnss.setUpLogging()}
            BLE.setUpLogging()
        }

        /*stopLogButton.setOnClickListener {
            loggingButton.isEnabled = true
            stopLogButton.isEnabled = false
            accelerometerSlider.isEnabled = true
            MagnetometerSlider.isEnabled = true
            BarometerSlider.isEnabled = true
            motionSensors.stopLogging()
            if (gnssToggle) {gnss.stopLogging(this)}
            BLE.stopLogging()
        }*/


/*
        val etTextToWatch: EditText = findViewById(R.id.etTextToWear)
        val sendButton: Button = findViewById(R.id.btnSend)
        val tvTexfFromWatch = findViewById<TextView>(R.id.tv_textFromWatch)
        mMessageClient = Wearable.getMessageClient(this)
        sendButton.setOnClickListener {
            var textToSend = etTextToWatch.text
            if (textToSend.isEmpty())
                Toast.makeText(this, "Add text", Toast.LENGTH_SHORT).show()
            else
                sendTextToWatch(textToSend.toString())
                Toast.makeText(this, "Text sent", Toast.LENGTH_SHORT).show()
        }

        mMessageClient.addListener {
            Log.d("phoneLogger", "it.data " + DataMap.fromByteArray(it.data).toString())
            val dataMap = DataMap.fromByteArray(it.data)
            tvTexfFromWatch.text = dataMap.getString("dataFromWatch")
        }*/

        var x1 = 0f
        var y1 = 0f
        var x2 = 0f
        var y2 = 0f

        findViewById<View>(R.id.activity_main_layout).setOnTouchListener { _, touchEvent ->
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

    fun checkPermissions() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH_SCAN
        )

        var allPermissionsGranted = true
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false
                break
            }
        }

        if (!allPermissionsGranted) {
            ActivityCompat.requestPermissions(this, permissions, 225)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {

            //location permission
            225 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                } else {
                    // Explain to the user that the feature is unavailable
                    AlertDialog.Builder(this)
                        .setTitle("Location permission denied")
                        .setMessage("Permission is denied.")
                        .setPositiveButton("OK",null)
                        .setNegativeButton("Cancel", null)
                        .show()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }
}