package com.mobilewizards.logging_app

import android.content.ContentValues
import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService


private lateinit var sensorManager : SensorManager

private var acSensor : Sensor? = null
private var biasSensor : Sensor? = null
private var gyroSensor : Sensor? = null
private var unCalGyroSensor : Sensor? = null
private var gravSensor : Sensor? = null
private var stepSensor : Sensor? = null

private var side: Float = Float.MIN_VALUE
private var upDown: Float = Float.MIN_VALUE
private var vertical: Float = Float.MIN_VALUE

private var sideNoBias : Float = Float.MIN_VALUE
private var upDownNoBias: Float = Float.MIN_VALUE
private var verticalNoBias: Float = Float.MIN_VALUE
private var sideBias : Float = Float.MIN_VALUE
private var upDownBias : Float = Float.MIN_VALUE
private var verticalBias : Float = Float.MIN_VALUE


private var gravX: Float = Float.MIN_VALUE
private var gravY: Float = Float.MIN_VALUE
private var gravZ: Float = Float.MIN_VALUE

private var rotationX: Float = Float.MIN_VALUE
private var rotationY: Float = Float.MIN_VALUE
private var rotationZ: Float = Float.MIN_VALUE

private var rotationXNoDrift: Float = Float.MIN_VALUE
private var rotationYNoDrift: Float = Float.MIN_VALUE
private var rotationZNoDrift: Float = Float.MIN_VALUE
private var rotationXDrift: Float = Float.MIN_VALUE
private var rotationYDrift: Float = Float.MIN_VALUE
private var rotationZDrift: Float = Float.MIN_VALUE

private var stepCount: Float = Float.MIN_VALUE

class MotionSensorsHandler: SensorEventListener{

    protected var context: Context
    var listenerActive = false

    constructor(context: Context) : super() {
        this.context = context.applicationContext
    }


    fun setUpSensors() {

        this.listenerActive = true

        sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager

        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            acSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorManager.registerListener(this, acSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }  else {

            Log.i("Does not have sensor for ACCELEROMETER", acSensor.toString())
        }
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED) != null){
            biasSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED)
            sensorManager.registerListener(this, biasSensor, SensorManager.SENSOR_DELAY_FASTEST)

        } else {

            Log.i("Does not have sensor for UNCALIBRATED ACCELEROMETER", biasSensor.toString())
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
            gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
            sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }  else {

            Log.i("Does not have sensor for GYROSCOPE", gyroSensor.toString())
        }

        if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED) != null){
            unCalGyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED)
            sensorManager.registerListener(this, unCalGyroSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }  else {

            Log.i("Does not have sensor for UNCALIBRATED GYROSCOPE", unCalGyroSensor.toString())
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null){
            gravSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
            sensorManager.registerListener(this, gravSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }   else {

            Log.i("Does not have sensor for GRAVITY", gravSensor.toString())
        }

        if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null){
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }   else {

            Log.i("Does not have sensor for STEP COUNTER", stepSensor.toString())
        }

    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event?.sensor?.type == acSensor?.type) {
            logAccelerometer(event)
        }

        if (event?.sensor?.type == biasSensor?.type) {
            logUnCalibratedAccelerometer(event)
        }

        /* if(event?.sensor?.type == Sensor.TYPE_GRAVITY){
             logGravity(event)
         }*/
        if (event?.sensor?.type == gyroSensor?.type) {
            logGyroscope(event)
        }

        if (event?.sensor?.type == unCalGyroSensor?.type) {
            logUnCalibratedGyroscope(event)
        }

        if (event?.sensor?.type == stepSensor?.type) {
            logSteps(event)
        }

    }

    private fun logAccelerometer(event: SensorEvent?) {
        if (event?.sensor?.type == acSensor?.type) {

            val sideTilt = event?.values?.get(0)
            if (sideTilt != side && sideTilt != null) {
                side = sideTilt
                Log.d("Tilting from side to side | Acceleration force along the X axis | Includes gravity", sideTilt.toString())

            }

            val upDownTilt = event?.values?.get(1)
            if (upDownTilt != upDown && upDownTilt != null) {
                upDown = upDownTilt
                Log.d("Tilting up or down | Acceleration force along the Y axis | Includes gravity", upDownTilt.toString())
            }

            val verticalTilt = event?.values?.get(2)
            if (verticalTilt != vertical && verticalTilt != null) {
                vertical = verticalTilt
                Log.d("Tilting vertically | Acceleration force along the Z axis | Includes gravity", verticalTilt.toString())
            }
        }
    }


    private fun logUnCalibratedAccelerometer(event: SensorEvent?) {

        if (event?.sensor?.type == biasSensor?.type && event?.sensor?.type != null) {

            val sideX = event.values[0]
            val upDownY = event.values[1]
            val verticalZ = event.values[2]
            val sideXB = event.values[3]
            val upDownYB = event.values[4]
            val verticalZB = event.values[5]

            if (sideX != sideNoBias) {
                sideNoBias = sideX
                Log.d("Acceleration along the X axis | NO bias compensation", sideX.toString())

            }

            if (upDownY != upDownNoBias) {
                upDownNoBias = upDownY
                Log.d("Acceleration along the Y axis | NO bias compensation", upDownY.toString())
            }

            if (verticalZ != verticalNoBias) {
                verticalNoBias = verticalZ
                Log.d("Acceleration along the Z axis | NO bias compensation", verticalZ.toString())
            }

            if (sideXB != sideBias) {
                sideBias = sideXB
                Log.d("Acceleration along the X axis | WITH bias compensation", sideXB.toString())

            }

            if (upDownYB != upDownBias) {
                upDownBias = upDownYB
                Log.d("Acceleration along the Y axis | WITH bias compensation", upDownYB.toString())
            }

            if (verticalZB != verticalBias) {
                verticalBias = verticalZB
                Log.d("Acceleration along the Z axis | WITH bias compensation", verticalZB.toString())
            }
        }
    }
    private fun logGravity(event: SensorEvent?) {

        if (event?.sensor?.type == gravSensor?.type && event?.sensor?.type != null) {
            val gravityX = event.values[0]
            val gravityY = event.values[1]
            val gravityZ = event.values[2]

            if (gravityX != gravX) {
                gravX = gravityX
                Log.d("Gravity along X", gravityX.toString())
            }

            if (gravityY != gravY) {
                gravY = gravityY
                Log.d("Gravity along Y", gravityY.toString())
            }

            if (gravityZ != gravZ) {
                gravZ = gravityZ
                Log.d("Gravity along Z", gravityZ.toString())
            }
        }
    }

    private fun logGyroscope(event: SensorEvent?) {

        if (event?.sensor?.type == gyroSensor?.type && event?.sensor?.type != null) {
            val rotX = event.values[0]
            val rotY = event.values[1]
            val rotZ = event.values[2]

            if (rotX != rotationX) {
                rotationX = rotX
                Log.d("Rotation along the X axis", rotX.toString())
            }

            if (rotY != rotationY) {
                rotationY = rotY
                Log.d("Rotation along Y axis", rotY.toString())
            }

            if (rotZ != rotationZ) {
                rotationZ = rotZ
                Log.d("Rotation along Z axis", rotZ.toString())
            }
        }
    }

    private fun logUnCalibratedGyroscope(event: SensorEvent?) {

        if (event?.sensor?.type == biasSensor?.type && event?.sensor?.type != null) {
            val noDriftX = event.values[0]
            val noDriftY = event.values[1]
            val noDriftZ = event.values[2]

            val driftX = event.values[3]
            val driftY = event.values[4]
            val driftZ = event.values[5]

            if (noDriftX != rotationXNoDrift) {
                rotationXNoDrift = noDriftX
                Log.d("Rotation along the X axis| NO drift compensation", noDriftX.toString())
            }

            if (noDriftY != rotationYNoDrift) {
                rotationYNoDrift =  noDriftY
                Log.d("Rotation along the Y axis| NO drift compensation", noDriftY.toString())
            }

            if (noDriftZ != rotationZNoDrift) {
                rotationZNoDrift = noDriftZ
                Log.d("Rotation along the Z axis| NO drift compensation", noDriftZ.toString())
            }

            if (driftX != rotationXDrift) {
                rotationXDrift = driftX
                Log.d("Rotation along the X axis| WITH drift compensation", driftX.toString())

            }

            if (driftY != rotationYDrift) {
                rotationYDrift = driftY
                Log.d("Rotation along the Y axis| WITH drift compensation", driftY.toString())
            }

            if (driftZ != rotationZDrift) {
                rotationZDrift = driftZ
                Log.d("Rotation along the Z axis| WITH drift compensation", driftZ.toString())
            }
        }
    }

    private fun logSteps(event: SensorEvent?) {

        if (event?.sensor?.type == stepSensor?.type && event?.sensor?.type != null) {
            val steps = event.values[0]

            if (steps != stepCount) {
                stepCount += steps
                Log.d("Steps", steps.toString())
            }
        }
    }

    fun stopLogging() {

        try {
            sensorManager.unregisterListener(this)
            this.listenerActive = false

        } catch(e: Exception){
            Log.e("Error", "An error occurred while saving motion sensors results")
        }
    }
    // TODO: Implement this when we know accuracy parameters
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        return
    }
}