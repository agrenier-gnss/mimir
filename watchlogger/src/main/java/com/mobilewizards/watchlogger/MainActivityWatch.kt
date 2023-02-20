package com.mobilewizards.logging_app

import android.app.Activity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.TextView
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import com.mobilewizards.logging_app.databinding.ActivityMainWatchBinding


class MainActivityWatch : Activity() {

    private lateinit var binding: ActivityMainWatchBinding
    private lateinit var mMessageClient: MessageClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainWatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mMessageClient = Wearable.getMessageClient(this)

        var text: TextView = findViewById(R.id.tv_watch)
        text.text = "Text:"
        Log.d("recievedData", "recievedData " + Wearable.getNodeClient(this).connectedNodes.toString())
//        mMessageClient.addListener {
//            MessageClient.OnMessageReceivedListener {
//                Log.d("recievedData", "got a message")
//                val dataMap = DataMap.fromByteArray(it.data)
//                val textFromPhone = dataMap.getString("/data")
//                Log.d("recievedData", textFromPhone)
//                text.text = textFromPhone
//            }
//        }

        mMessageClient.addListener {
            val dataMap = DataMap.fromByteArray(it.data)
            val textFromPhone = dataMap.getDataMap("/message")
            text.text = textFromPhone.toString()
        }
    }
}