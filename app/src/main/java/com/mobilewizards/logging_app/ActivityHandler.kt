package com.mobilewizards.logging_app

import android.content.Context
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ActivityHandler(private val context: Context) {

    //this class handles logging data and log events all from one class

    private val buttonState = MutableLiveData<Boolean>(false)

    fun getButtonState(): LiveData<Boolean> {
        return buttonState
    }

    fun toggleButton() {
        buttonState.value = !(buttonState.value ?: false)
    }

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

    //todo: neliöiden skaalaus näytön kokoon. nappien synkkaus. log event määrien kirjaus.

}