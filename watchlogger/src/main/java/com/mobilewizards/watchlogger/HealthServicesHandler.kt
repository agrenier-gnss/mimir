package com.mobilewizards.watchlogger

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class HealthServicesHandler: SensorEventListener{

    private lateinit var mHeartRateSensor: Sensor
    private lateinit var mSensorManager: SensorManager
    private lateinit var context: Context

    constructor(context: Context) {
        this.context = context.applicationContext
    }

    fun getHeartRate() {
        mSensorManager = context.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        if (mHeartRateSensor == null) {
            Log.d("watchLogger", "heart rate is null")
        } else {
            mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST)
        }
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        for (event in sensorEvent?.values!!) {
            Log.d("heart", event.toString())
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        TODO("Not yet implemented")
    }
}