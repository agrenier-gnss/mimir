package com.mobilewizards.watchlogger

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.mobilewizards.watchlogger.databinding.ActivityMainWatchBinding

class MainActivityWatch : Activity() {

    private lateinit var binding: ActivityMainWatchBinding
    private lateinit var mMessageClient: MessageClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainWatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mMessageClient = Wearable.getMessageClient(this)

        var text: TextView = findViewById(R.id.tv_watch)

        mMessageClient.addListener {
            MessageClient.OnMessageReceivedListener {
                val dataMap = DataMap.fromByteArray(it.data)
                val textFromPhone = dataMap.getString("data")
                text.text = textFromPhone
            }
        }
    }
}