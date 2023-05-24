package com.mobilewizards.logging_app

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.*

//this class handles logging data and log events all from one class
object ActivityHandler{

    private var isLogging: Boolean = false

    private var IMUFrequency: Int = 10
    private var barometerFrequency: Int = 1
    private var magnetometerFrequency: Int = 1

    //Boolean values to enable or disable sensors.
    private var IMUToggle: Boolean = true
    private var GNSSToggle: Boolean = true
    private var barometerToggle: Boolean = true
    private var magnetometerToggle: Boolean = true
    private var BLEToggle: Boolean = true

    //Lists where sensors will be put when logging.
    var gnssSensor = mutableListOf<GnssHandler>()
    var imuSensor = mutableListOf<MotionSensorsHandler>()
    var bleSensor = mutableListOf<BLEHandler>()

    // Amount of logged events
    private var IMULogs: Int = 0
    private var GNSSLogs: Int = 0
    private var barometerLogs: Int = 0
    private var magnetometerLogs: Int = 0
    private var BLELogs: Int = 0

    // Survey start time
    private var surveyStartTime: String = "Time not set"

    // Keeps track of logging_button state
    private val buttonState = MutableLiveData<Boolean>(false)

    fun getButtonState(): LiveData<Boolean> {
        return buttonState
    }

    fun toggleButton(context: Context) {
        buttonState.value = !(buttonState.value ?: false)

        if(buttonState.value==true){
            startLogging(context)
        }
        else{
            stopLogging(context)
        }
    }

    fun isLogging(): Boolean{
        return isLogging
    }

    fun startLogging(context: Context){

        val motionSensors = MotionSensorsHandler(context)
        val gnss = GnssHandler(context)
        val ble =  BLEHandler(context)

        gnssSensor.add(gnss)
        imuSensor.add(motionSensors)
        bleSensor.add(ble)

        if(IMUToggle || getToggle("Magnetometer") || getToggle("Barometer"))
            {motionSensors.setUpSensors(IMUFrequency, magnetometerFrequency, barometerFrequency)}
        if (GNSSToggle) {gnss.setUpLogging()}
        if(BLEToggle){ble.setUpLogging()}

        isLogging = true
        setSurveyStartTime()
    }

    fun stopLogging(context: Context){

        if (GNSSToggle) {
            gnssSensor[0].stopLogging(context)}
        if(IMUToggle || getToggle("Magnetometer") || getToggle("Barometer")){
            imuSensor[0].stopLogging()
        }
        if(BLEToggle){
            bleSensor[0].stopLogging()
        }

        gnssSensor.clear()
        imuSensor.clear()
        bleSensor.clear()
        isLogging = false
    }

    // Get info on whether the sensor will be logged or not
    fun getToggle(tag: String): Boolean{
        if(tag.equals("GNSS")){
            return GNSSToggle
        }
        else if(tag.equals("IMU")){
            return IMUToggle
        }
        else if(tag.equals("Barometer")){
            return barometerToggle
        }
        else if(tag.equals("Magnetometer")){
            return magnetometerToggle
        }
        else if(tag.equals("Bluetooth")){
            return BLEToggle
        }
        return false
    }

    // Toggle sensor status between true and false whether it will be logged or not
    fun setToggle(tag: String){
        if(tag.equals("GNSS")){
             GNSSToggle = !GNSSToggle
        }
        else if(tag.equals("IMU")){
            IMUToggle = !IMUToggle
        }
        else if(tag.equals("Barometer")){
            barometerToggle = !barometerToggle
        }
        else if(tag.equals("Magnetometer")){
            magnetometerToggle = !magnetometerToggle
        }
        else if(tag.equals("Bluetooth")){
            BLEToggle = !BLEToggle
        }
    }

    fun getFrequency(tag: String): Int{
        if(tag.equals("IMU")){
            return IMUFrequency
        }
        else if(tag.equals("Barometer")){
            return barometerFrequency
        }
        else if(tag.equals("Magnetometer")){
            return magnetometerFrequency
        }
        return 0
    }

    fun setFrequency(tag: String, value: Int){

        if(tag.equals("IMU")){
            IMUFrequency = value


        }
        else if(tag.equals("Barometer")){
            barometerFrequency = value
        }
        else if(tag.equals("Magnetometer")){
            magnetometerFrequency = value
        }
    }

    fun getLogData(tag: String): String {
        if(tag.equals("GNSS")) {
            return GNSSLogs.toString()
        } else if(tag.equals("IMU")){
            return IMULogs.toString()
        } else if(tag.equals("Barometer")){
            return barometerLogs.toString()
        } else if(tag.equals("Magnetometer")){
            return magnetometerLogs.toString()
        } else if(tag.equals("Bluetooth")) {
            return BLELogs.toString()
        }
        return 0.toString()
    }

    fun setSurveyStartTime() {
        val currentTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("HH:mm")
        surveyStartTime = dateFormat.format(currentTime)
    }

    fun getSurveyStartTime(): String {
        return surveyStartTime
    }
}