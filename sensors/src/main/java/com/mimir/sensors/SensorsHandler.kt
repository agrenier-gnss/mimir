package com.mimir.sensors

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import java.util.Collections.synchronizedList

// For Google Pixel Watch
var ECG_SENSOR_NAME = "AFE4950 ECG Sensor"
var PPG_SENSOR_NAME = "AFE4950 PPG Sensor"
var GAL_SENSOR_NAME = "AFE4950 Galvanic Skin Response"

// For Samsung Galaxy Watch 6
//var ECG_SENSOR_NAME_GALAXY = "AFE4500S ECG"

class SensorsHandler(val context: Context) {

    var mSensors = mutableListOf<CustomSensor>()
    val mSensorsResults = synchronizedList(mutableListOf<Any>())

    private var fileHandler: FileHandler
    private var handlerThread: HandlerThread = HandlerThread("").apply {
        start()
        fileHandler = FileHandler(context, looper)
    }

    // ---------------------------------------------------------------------------------------------

    init {

    }

    // ---------------------------------------------------------------------------------------------

    fun addSensor(_type : SensorType, _samplingFrequency : Int = 1000) : Boolean {
        var success = true
        when(_type){
            SensorType.TYPE_ACCELEROMETER ->
                mSensors.add(MotionSensor(this.context, fileHandler, _type, "ACC", _samplingFrequency, mSensorsResults))
            SensorType.TYPE_GYROSCOPE ->
                mSensors.add(MotionSensor(this.context, fileHandler, _type, "GYR", _samplingFrequency, mSensorsResults))
            SensorType.TYPE_MAGNETIC_FIELD ->
                mSensors.add(MotionSensor(this.context, fileHandler, _type, "MAG", _samplingFrequency, mSensorsResults))
            SensorType.TYPE_ACCELEROMETER_UNCALIBRATED ->
                mSensors.add(UncalibratedMotionSensor(this.context, fileHandler, _type, "ACC_UNCAL", _samplingFrequency, mSensorsResults))
            SensorType.TYPE_GYROSCOPE_UNCALIBRATED ->
                mSensors.add(UncalibratedMotionSensor(this.context, fileHandler, _type, "GYR_UNCAL", _samplingFrequency, mSensorsResults))
            SensorType.TYPE_MAGNETIC_FIELD_UNCALIBRATED ->
                mSensors.add(UncalibratedMotionSensor(this.context, fileHandler, _type, "MAG_UNCAL", _samplingFrequency, mSensorsResults))

            SensorType.TYPE_PRESSURE ->
                mSensors.add(EnvironmentSensor(this.context, fileHandler, _type, "PSR", _samplingFrequency, mSensorsResults))

            SensorType.TYPE_GNSS_LOCATION ->
                mSensors.add(GnssLocationSensor(this.context, fileHandler, mSensorsResults, LocationManager.GPS_PROVIDER))
            SensorType.TYPE_FUSED_LOCATION ->
                mSensors.add(GnssLocationSensor(this.context, fileHandler, mSensorsResults, LocationManager.FUSED_PROVIDER))
            SensorType.TYPE_GNSS_MEASUREMENTS ->
                mSensors.add(GnssMeasurementSensor(this.context, fileHandler, mSensorsResults))
            SensorType.TYPE_GNSS_MESSAGES ->
                mSensors.add(GnssNavigationMessageSensor(this.context, fileHandler, mSensorsResults))

            SensorType.TYPE_BLUETOOTH ->
                mSensors.add(BluetoothSensor(this.context, fileHandler, mSensorsResults))

            SensorType.TYPE_HEART_RATE ->
                mSensors.add(HeartRateSensor(this.context, fileHandler, _type, "ECG", _samplingFrequency, mSensorsResults))

            SensorType.TYPE_SPECIFIC_ECG ->
                mSensors.add(SpecificSensor(this.context, fileHandler, ECG_SENSOR_NAME, SensorType.TYPE_SPECIFIC_ECG, "ECG", _samplingFrequency, mSensorsResults))
            SensorType.TYPE_SPECIFIC_PPG ->
                mSensors.add(SpecificSensor(this.context, fileHandler, PPG_SENSOR_NAME, SensorType.TYPE_SPECIFIC_PPG, "PPG", _samplingFrequency, mSensorsResults))
            SensorType.TYPE_SPECIFIC_GSR ->
                mSensors.add(SpecificSensor(this.context, fileHandler, GAL_SENSOR_NAME, SensorType.TYPE_SPECIFIC_GSR, "GAL", _samplingFrequency, mSensorsResults))

            SensorType.TYPE_STEP_COUNTER ->
                mSensors.add(StepSensor(this.context, fileHandler, _type, "STEP_C", _samplingFrequency, mSensorsResults))
            SensorType.TYPE_STEP_DETECTOR ->
                mSensors.add(StepSensor(this.context, fileHandler, _type, "STEP_D", _samplingFrequency, mSensorsResults))

            else -> {
                Log.w("SensorsHandler", "Sensor type $_type not supported.")
                success = false
            }
        }

        return success
    }

    // ---------------------------------------------------------------------------------------------

    fun startLogging(){

        // Enable logging in sensors
        for (sensor in mSensors){
            sensor.registerSensor()
        }

        Log.i("SensorsHandler", "Logging started")
    }

    // ---------------------------------------------------------------------------------------------

    fun stopLogging(){

        // disable logging in sensors
        for (_sensor in mSensors){
            _sensor.unregisterSensor()
        }

        // Write to file
        fileHandler.closeFile()

        Log.i("SensorsHandler", "Logging stopped")
    }



}