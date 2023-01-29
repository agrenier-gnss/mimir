package com.mobilewizards.logging_app

import android.util.Log

class CounterThread : Thread() {
    private var running = false
    private var count = 0

    override fun run() {
        running = true
        while (running) {
            count++
            Log.d("CounterThread", "Count: $count")
            sleep(1000)
        }
    }

    fun cancel() {
        running = false
    }
}

var counterThread: CounterThread? = null
