package com.mobilewizards.logging_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

//this class handles logging data and log events all from one class
object ActivityHandler{

    private var isLogging: Boolean = false

    private var IMUFrequency: Int = 10
    private var barometerFrequency: Int = 1
    private var magneometreFrequency: Int = 1

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
    }

    fun getIsLogging(): Boolean{
        return isLogging
    }

    //Functions to both logging and stopping it.
    fun startLogging(){

    }

    fun stopLogging(){

    }

    //Next 4 functions will change availability of sensors. They are used in switches in MainActivity
    fun setGnssToggle(enabled: Boolean){
        GNSSToggle = enabled
    }

    fun getGnssToggle(): Boolean{
        return GNSSToggle
    }

    fun setBarometerToggle(enabled: Boolean){
        barometerToggle = enabled
    }

    fun getBarometerToggle(): Boolean{
        return barometerToggle
    }

    fun setIMUToggle(enabled: Boolean){
        IMUToggle = enabled
    }

    fun getIMUToggle(): Boolean{
        return IMUToggle
    }

    fun setMagnetometerToggle(enabled: Boolean){
        magnetometerToggle = enabled
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
        return magneometreFrequency
    }
    fun setMagnetometerFrequency(Value: Int){
        magneometreFrequency = Value
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