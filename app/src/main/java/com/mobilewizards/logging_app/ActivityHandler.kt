package com.mobilewizards.logging_app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

//this class handles logging data and log events all from one class
class ActivityHandler private constructor() {

    //keeps track of the button state and synchronises them between activities
    private val buttonState = MutableLiveData<Boolean>(false)
    fun getButtonState(): LiveData<Boolean> {
        return buttonState
    }
    fun toggleButton() {
        buttonState.value = !(buttonState.value ?: false)
    }

    //this should be a singleton, but is not working
    companion object {
        private var INSTANCE: ActivityHandler? = null

        fun getInstance(): ActivityHandler {
            if (INSTANCE == null) {
                INSTANCE = ActivityHandler()
            }
            return INSTANCE as ActivityHandler
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