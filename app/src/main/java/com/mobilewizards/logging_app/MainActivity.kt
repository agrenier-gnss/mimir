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
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var mMessageClient: MessageClient
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("phoneLogger", "onCreate called")
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        this.checkPermissions()

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

            var sensorStateTextView = row.findViewById<TextView>(R.id.sensorState)
            setStateTextview(sensorSwitch.isChecked, sensorStateTextView)

            val row2 = tableLayout.getChildAt(1) as TableRow
            val description = row2.findViewById<TextView>(R.id.description)


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
            }

            // Remove the tableLayout's parent, if it has one
            (tableLayout.parent as? ViewGroup)?.removeView(tableLayout)

            // Add the TableLayout to the parent view
            parentView.addView(tableLayout)

        }
        /*

        val gnssStateTextView = findViewById<TextView>(R.id.gnssState)
        val gnssSwitch: SwitchCompat = findViewById(R.id.gnssSwitch)

        gnssSwitch.isChecked = ActivityHandler.getGnssToggle()
        setStateTextview(gnssSwitch.isChecked,gnssStateTextView)

        gnssSwitch.setOnCheckedChangeListener { _, isChecked ->
            ActivityHandler.setGnssToggle()
            setStateTextview(gnssSwitch.isChecked,gnssStateTextView)
        }



        val IMUStateTextView = findViewById<TextView>(R.id.IMUState)
        val IMUSliderTextView = findViewById<TextView>(R.id.IMUValue)
        val IMUSlider = findViewById<SeekBar>(R.id.sliderIMU)
        IMUSlider.min = 10
        IMUSlider.max = 100
        IMUSlider.progress = ActivityHandler.getIMUFrequency()
        IMUSliderTextView.setText(IMUSlider.progress.toString()+"hz")
        IMUSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("IMUPROGRESS", IMUSlider.progress.toString())
                val accelerometerMap = DataMap().apply{
                    putInt("accelerometer",IMUSlider.progress)
                }
                ActivityHandler.setIMUFrequency(IMUSlider.progress)
                IMUSliderTextView.setText(IMUSlider.progress.toString()+"hz")

                //sendParameterToWatch(accelerometerMap)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val IMUSwitch = findViewById<SwitchCompat>(R.id.IMUSwitch)
        IMUSwitch.isChecked = ActivityHandler.getIMUToggle()
        setStateTextview(IMUSwitch.isChecked, IMUStateTextView)

        IMUSwitch.setOnCheckedChangeListener { _, isChecked ->
            ActivityHandler.setIMUToggle()
            setStateTextview(IMUSwitch.isChecked, IMUStateTextView)

        }

        val barometerStateTextView = findViewById<TextView>(R.id.baroState)
        val barometerSliderTextView = findViewById<TextView>(R.id.baroValue)
        val BarometerSlider = findViewById<SeekBar>(R.id.sliderBarometer)
        BarometerSlider.min = 1
        BarometerSlider.max = 10
        BarometerSlider.progress = ActivityHandler.getBarometerFrequency()
        barometerSliderTextView.setText(BarometerSlider.progress.toString()+"hz")

        BarometerSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("BAROSLIDERPROGRESS", BarometerSlider.progress.toString())
                val barometerMap = DataMap().apply{
                    putInt("barometer",BarometerSlider.progress)
                }
                ActivityHandler.setBarometerFrequency(BarometerSlider.progress)
                barometerSliderTextView.setText(BarometerSlider.progress.toString()+"hz")
                //sendParameterToWatch(barometerMap)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val barometerSwitch = findViewById<SwitchCompat>(R.id.baroSwitch)
        barometerSwitch.isChecked = ActivityHandler.getBarometerToggle()
        setStateTextview(barometerSwitch.isChecked, barometerStateTextView)

        barometerSwitch.setOnCheckedChangeListener { _, isChecked ->
            ActivityHandler.setBarometerToggle()
            setStateTextview(barometerSwitch.isChecked, barometerStateTextView)
        }

        val magnetometerStateTextView = findViewById<TextView>(R.id.magnetoState)
        val magnetometerSliderTextView = findViewById<TextView>(R.id.magnetoValue)
        val MagnetometerSlider = findViewById<SeekBar>(R.id.sliderMagnetometer)
        MagnetometerSlider.min = 1
        MagnetometerSlider.max = 10
        MagnetometerSlider.progress = ActivityHandler.getMagnetometerFrequency()
        magnetometerSliderTextView.text = MagnetometerSlider.progress.toString()+"hz"

        MagnetometerSlider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d("MAGNESLIDERPROGRESS", MagnetometerSlider.progress.toString())
                val magnetometerMap = DataMap().apply{
                    putInt("magnetometer",MagnetometerSlider.progress)
                }
                ActivityHandler.setMagnetometerFrequency(MagnetometerSlider.progress)
                magnetometerSliderTextView.setText(MagnetometerSlider.progress.toString()+"hz")
                //sendParameterToWatch(magnetometerMap)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        val magnetometerSwitch = findViewById<SwitchCompat>(R.id.magnetoSwitch)
        magnetometerSwitch.isChecked = ActivityHandler.getMagnetometerToggle()
        setStateTextview(magnetometerSwitch.isChecked, magnetometerStateTextView)

        magnetometerSwitch.setOnCheckedChangeListener { _, isChecked ->
            ActivityHandler.setMagnetometerToggle()
            setStateTextview(magnetometerSwitch.isChecked, magnetometerStateTextView)
        }
        */


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

    fun setStateTextview(enabled: Boolean,textview: TextView) {
        if (enabled) {
            textview.text = "Enabled"
        } else {
            textview.text = "Disabled"
        }
    }
}