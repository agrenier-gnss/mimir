package com.mobilewizards.logging_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

//this class handles logging data and log events all from one class
object ActivityHandler{

    private lateinit var motionSensors: MotionSensorsHandler
    private lateinit var gnss: GnssHandler
    private lateinit var ble: BLEHandler
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

    //keeps track of the button state and synchronises them between activities
    private val buttonState = MutableLiveData<Boolean>(false)
    fun getButtonState(): LiveData<Boolean> {
        return buttonState
    }

    fun toggleButton() {
        buttonState.value = !(buttonState.value ?: false)
        /*if(buttonState.value==true){
            startLogging()
        }
        else{
            stopLogging()
        }*/
    }

    fun getIsLogging(): Boolean{
        return isLogging
    }

    //Functions to both logging and stopping it.
    fun startLogging(){
        if(IMUToggle){motionSensors.setUpSensors()}
        if (GNSSToggle) {gnss.setUpLogging()}
        if(BLEToggle){ble.setUpLogging()}
    }

    fun stopLogging(){
        if(IMUToggle){motionSensors.setUpSensors()}
        if (GNSSToggle) {gnss.setUpLogging()}
        if(BLEToggle){ble.setUpLogging()}

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

}