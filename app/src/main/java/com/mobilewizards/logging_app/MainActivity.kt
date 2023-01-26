package com.mobilewizards.logging_app

import android.content.Intent
import android.hardware.Sensor
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.hardware.SensorManager
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import android.widget.Button


class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var gyroSensorM: SensorManager
    private lateinit var gravSensorM: SensorManager
    private lateinit var stepSensorM: SensorManager

    private var side: Float = Float.MIN_VALUE
    private var upDown: Float = Float.MIN_VALUE
    private var vertical: Float = Float.MIN_VALUE

    private var gravX: Float = Float.MIN_VALUE
    private var gravY: Float = Float.MIN_VALUE
    private var gravZ: Float = Float.MIN_VALUE

    private var rotationX: Float = Float.MIN_VALUE
    private var rotationY: Float = Float.MIN_VALUE
    private var rotationZ: Float = Float.MIN_VALUE

    private var stepCount: Float = Float.MIN_VALUE



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loggingButton = findViewById<Button>(R.id.startLogButton)
        val stopLogButton = findViewById<Button>(R.id.stopLogButton)


        loggingButton.setOnClickListener{
            Log.d("start logging", "Start logging")
            setUpSensors()
        }

        stopLogButton.setOnClickListener {
            onPause()
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
                val setupIntent = Intent(this@MainActivity, SetupActivity::class.java)
                startActivity(setupIntent)
            }
        }
        return true
    }

    private fun setUpSensors(){

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also{
            sensorManager.registerListener(this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST)
        }

        gyroSensorM = getSystemService(SENSOR_SERVICE) as SensorManager
        gyroSensorM.getDefaultSensor(Sensor.TYPE_GYROSCOPE)?.also{
            gyroSensorM.registerListener(this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST)
        }

        gravSensorM = getSystemService(SENSOR_SERVICE) as SensorManager
        gravSensorM.getDefaultSensor(Sensor.TYPE_GRAVITY)?.also{
            gravSensorM.registerListener(this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST)
        }

        stepSensorM = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensorM.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.also{
            stepSensorM.registerListener(this,
                it,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST)
        }


    }
    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            logAccelerometer(event)
        }
        /* if(event?.sensor?.type == Sensor.TYPE_GRAVITY){
            logGravity(event)
        }*/
         if(event?.sensor?.type == Sensor.TYPE_GYROSCOPE){
            logGyroscope(event)
        }
        if(event?.sensor?.type == Sensor.TYPE_STEP_COUNTER){
            logSteps(event)
        }


    }

    private fun logAccelerometer(event: SensorEvent?){
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val sideTilt = event.values[0]

            if (sideTilt != side) {
                side = sideTilt
                Log.d("Tilting from side to side", sideTilt.toString())

            }

            val upDownTilt = event.values[1]
            if(upDownTilt != upDown) {
                upDown = upDownTilt
                Log.d("Tilting up or down", upDownTilt.toString())
            }

            val verticalTilt = event.values[2]
            if(verticalTilt != vertical) {
                vertical = verticalTilt
                Log.d("Tilting vertically", verticalTilt.toString())
            }
        }

    }

    private fun logGravity(event: SensorEvent?){

        if (event?.sensor?.type == Sensor.TYPE_GRAVITY) {
            val gravityX = event.values[0]

            if (gravityX != gravX) {
                gravX = gravityX
                Log.d("Gravity along X", gravityX.toString())

            }

            val gravityY = event.values[1]

            if (gravityY != gravY) {
                gravY = gravityY
                Log.d("Gravity along Y", gravityY.toString())

            }

            val gravityZ = event.values[2]

            if (gravityZ != gravZ) {
                gravZ = gravityZ
                Log.d("Gravity along Z", gravityZ.toString())

            }
        }

    }

    private fun logGyroscope(event: SensorEvent?){

        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            val rotX = event.values[0]

            if (rotX != rotationX) {
                rotationX = rotX
                Log.d("Rotation along X", rotX.toString())

            }

            val rotY = event.values[1]

            if (rotY != rotationY) {
                rotationY = rotY
                Log.d("Rotation along Y", rotY.toString())

            }

            val rotZ = event.values[2]

            if (rotZ != rotationZ) {
                rotationZ = rotZ
                Log.d("Rotation along Z", rotZ.toString())

            }
        }

    }

    private fun logSteps(event: SensorEvent?) {

        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val steps = event.values[0]

            if (steps != stepCount) {
                stepCount += steps
                Log.d("Steps", steps.toString())

            }

        }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }

    override fun onPause() {
        sensorManager.unregisterListener(this)
        side = Float.MIN_VALUE
        upDown = Float.MIN_VALUE
        vertical = Float.MIN_VALUE

        gravSensorM.unregisterListener(this)
        gravX = Float.MIN_VALUE
        gravY = Float.MIN_VALUE
        gravZ = Float.MIN_VALUE

        gyroSensorM.unregisterListener(this)
        rotationX = Float.MIN_VALUE
        rotationY = Float.MIN_VALUE
        rotationZ = Float.MIN_VALUE

        stepSensorM.unregisterListener(this)
        stepCount = Float.MIN_VALUE

        Log.d("Stop logging", "Stop logging")

        super.onPause()
    }

}