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
        ble.setUpLogging()
    }

    fun stopLogging(){
        if(IMUToggle){motionSensors.setUpSensors()}
        if (GNSSToggle) {gnss.setUpLogging()}
        ble.setUpLogging()
    }

    //Next 4 functions will change availability of sensors. They are used in switches in MainActivity
    fun setGnssToggle(){
        GNSSToggle = !GNSSToggle
    }

    fun getGnssToggle(): Boolean{
        return GNSSToggle
    }

    fun setBarometerToggle(){
        barometerToggle = !barometerToggle
    }

    fun getBarometerToggle(): Boolean{
        return barometerToggle
    }

    fun setIMUToggle(){
        IMUToggle = !IMUToggle
    }

    fun getIMUToggle(): Boolean{
        return IMUToggle
    }

    fun setMagnetometerToggle(){
        magnetometerToggle = !magnetometerToggle
    }

    fun getMagnetometerToggle(): Boolean{
        return magnetometerToggle
    }

    fun getIMUFrequency(): Int{
        return IMUFrequency
    }

    fun setIMUFrequency(Value: Int){
        IMUFrequency = Value
    }

    fun getBarometerFrequency(): Int{
        return barometerFrequency
    }

    fun setBarometerFrequency(Value: Int){
        barometerFrequency = Value
    }

    fun getMagnetometerFrequency(): Int{
        return magnetometerFrequency
    }
    fun setMagnetometerFrequency(Value: Int){
        magnetometerFrequency = Value
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