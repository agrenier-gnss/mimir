package com.mobilewizards.logging_app

import android.annotation.SuppressLint
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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

        this.checkPermissions()


        // Check if thread is alive to rightfully enable/disable buttons
        if (counterThread?.isAlive == true) {
            // Implementation of code that require concurrent threads to be running
        }

        val gnssButton = findViewById<Button>(R.id.startGNSSButton)
        val imuButton = findViewById<Button>(R.id.startIMUButton)
        val magnetometerButton = findViewById<Button>(R.id.startMagnetometerButton)
        val barometerButton = findViewById<Button>(R.id.startBarometerButton)

        var gnssToggle = true
        var imuToggle = true
        var magnetometerToggle = true
        var barometerToggle = true

        val loggingButton = findViewById<Button>(R.id.startLogButton)
        val stopLogButton = findViewById<Button>(R.id.stopLogButton)
        val motionSensors = MotionSensorsHandler(this)
        val gnss = GnssHandler(this)
        val IMUSlider = findViewById<SeekBar>(R.id.sliderIMU)
        val MagnetometerSlider = findViewById<SeekBar>(R.id.sliderMagnetometer)
        val BarometerSlider = findViewById<SeekBar>(R.id.sliderBarometer)
        val BLE = BLEHandler(this)

        loggingButton.setOnClickListener{
            loggingButton.isEnabled = false
            stopLogButton.isEnabled = true
            gnssButton.isEnabled = false
            imuButton.isEnabled = false
            magnetometerButton.isEnabled = false
            barometerButton.isEnabled = false
            IMUSlider.isEnabled = false
            MagnetometerSlider.isEnabled = false
            BarometerSlider.isEnabled = false
            Log.d("start logging", "Start logging")
            motionSensors.setUpSensors()
            if (gnssToggle) {gnss.setUpLogging()}
            BLE.setUpLogging()
        }

        stopLogButton.setOnClickListener {
            loggingButton.isEnabled = true
            stopLogButton.isEnabled = false
            gnssButton.isEnabled = true
            imuButton.isEnabled = true
            magnetometerButton.isEnabled = true
            barometerButton.isEnabled = true
            IMUSlider.isEnabled = true
            MagnetometerSlider.isEnabled = true
            BarometerSlider.isEnabled = true
            motionSensors.stopLogging()
            if (gnssToggle) {gnss.stopLogging(this)}
            BLE.stopLogging()
        }


        gnssButton.setOnClickListener {
            if (!gnssToggle) {
                gnssToggle = true
                gnssButton.text = "GNSS"
            } else {
                gnssToggle = false
                gnssButton.text = "GNSS - Disabled"
            }
        }

        imuButton.setOnClickListener {
            if (!imuToggle) {
                imuToggle = true
                imuButton.text = "IMU"
            } else {
                imuToggle = false
                imuButton.text = "IMU - Disabled"
            }
        }

        magnetometerButton.setOnClickListener {
            if (!magnetometerToggle) {
                magnetometerToggle = true
                magnetometerButton.text = "Magnetometer"
            } else {
                magnetometerToggle = false
                magnetometerButton.text = "Magnetometer - Disabled"
            }
        }

        barometerButton.setOnClickListener {
            if (!barometerToggle) {
                barometerToggle = true
                barometerButton.text = "Barometer"
            } else {
                barometerToggle = false
                barometerButton.text = "Barometer - Disabled"
            }
        }

        IMUSlider.min = 10
        IMUSlider.max = 100
        IMUSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("IMUSLIDERPROGRESS", IMUSlider.progress.toString())
                val IMUMap = DataMap().apply{
                    putInt("imu",IMUSlider.progress)
                }
                sendParameterToWatch(IMUMap)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        MagnetometerSlider.min = 1
        MagnetometerSlider.max = 10
        MagnetometerSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("MAGNESLIDERPROGRESS", MagnetometerSlider.progress.toString())
                val magnetometerMap = DataMap().apply{
                    putInt("magnetometer",MagnetometerSlider.progress)
                }
                sendParameterToWatch(magnetometerMap)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        BarometerSlider.min = 1
        BarometerSlider.max = 10
        BarometerSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("BAROSLIDERPROGRESS", BarometerSlider.progress.toString())
                val barometerMap = DataMap().apply{
                    putInt("barometer",BarometerSlider.progress)
                }
                sendParameterToWatch(barometerMap)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


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