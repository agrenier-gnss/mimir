package com.mobilewizards.logging_app

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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
    private var timeToggle: Boolean = true

    //Lists where sensors will be put when logging.
    private var gnssSensor = mutableListOf<GnssHandler>()
    private var imuSensor = mutableListOf<MotionSensorsHandler>()
    private var bleSensor = mutableListOf<BLEHandler>()

    // Amount of logged events
    private var IMULogs: Int = 0
    private var GNSSLogs: Int = 0
    private var barometerLogs: Int = 0
    private var magnetometerLogs: Int = 0
    private var BLELogs: Int = 0

    // Time related
    private val myTimer = MyTimer()
    private var surveyDuration: Int = 0 // Survey duration in seconds
    private var surveyStartTime: String = "Time not set" // Survey start time

    //keeps track of the button state and synchronises them between activities
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

    fun getIsLogging(): Boolean{
        return isLogging
    }

    //Functions to both logging and stopping it.
    fun startLogging(context: Context){
        val motionSensors = MotionSensorsHandler(context)
        val gnss= GnssHandler(context)
        val ble =  BLEHandler(context)
        gnssSensor.add(gnss)
        imuSensor.add(motionSensors)
        bleSensor.add(ble)
        if(IMUToggle){motionSensors.setUpSensors(IMUFrequency, magnetometerFrequency,
            barometerFrequency)}
        if (GNSSToggle) {gnss.setUpLogging()}
        if(BLEToggle){ble.setUpLogging()}
        isLogging = true
        setSurveyStartTime()

        myTimer.startTimer {
            surveyDuration++
        }
    }

    fun stopLogging(context: Context){
        if (GNSSToggle) {
            gnssSensor[0].stopLogging(context)}
        if(IMUToggle){
            imuSensor[0].stopLogging()
        }
        if(BLEToggle){
            bleSensor[0].stopLogging()
        }
        gnssSensor.clear()
        imuSensor.clear()
        bleSensor.clear()
        isLogging = false

        myTimer.stopTimer()
    }

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
        else if(tag.equals("Time")) {
            return timeToggle
        }
        return false
    }

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

    //counter for keeping time on logging
    var counterThread : CounterThread? = null

    // Check if thread is alive to rightfully enable/disable buttons
    fun startCounterThread() {
        if (counterThread?.isAlive == true) {
            // Implementation of code that require concurrent threads to be running
        }
        counterThread = CounterThread()
        counterThread?.start()
    }

    fun stopCounterThread() {
        if (counterThread != null) {
            counterThread?.cancel()
            counterThread = null
        }
    }

    class MyTimer {
        private val timer = Timer()

        private var currentTime: LocalTime = LocalTime.MIN

        fun startTimer(callback: () -> Unit) {
            timer.scheduleAtFixedRate(object : TimerTask() {
                override fun run() {
                    updateTime()
                    callback()
                }
            }, 0, 1000) // update every second
        }

        fun stopTimer() {
            timer.cancel()
        }

        fun getCurrentLocalTime(): LocalTime {
            return currentTime
        }

        private fun updateTime() {
            currentTime = currentTime.plusSeconds(1)
        }
    }

    fun getLogData(tag: String): String {
        if(tag.equals("Time")) {
            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            return LocalTime.ofSecondOfDay(surveyDuration.toLong()).format(formatter)
        } else if(tag.equals("GNSS")) {
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