package com.mobilewizards.logging_app

import android.app.Activity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
        var text: TextView = findViewById(R.id.tv_watch)
        text.text = "Text:"

        // Logcat all connected nodes. Phone should show here
        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            Log.d(
                "watchLogger", nodes.toString()
            )
        }
        // Get messages from phone
        mMessageClient = Wearable.getMessageClient(this)
        mMessageClient.addListener {
            Log.d("watchLogger", "it.data " + DataMap.fromByteArray(it.data).toString())
            val dataMap = DataMap.fromByteArray(it.data)
            text.text = dataMap.getString("data")
        }

        // Send messages to phone
        val sendButton = findViewById<Button>(R.id.btn_send)
        sendButton.setOnClickListener {
            var textToSend = "This is a test text sent from watch"
            sendTextToWatch(textToSend.toString())
            Toast.makeText(this, "Text sent", Toast.LENGTH_SHORT).show()
        }
    }
    private fun sendTextToWatch(text: String) {
        val dataMap = DataMap().apply {
            putString("dataFromWatch", text)
        }
        val dataByteArray = dataMap.toByteArray()

        Wearable.getNodeClient(this).connectedNodes.addOnSuccessListener { nodes ->
            for (node in nodes) {
                Log.d("watchLogger", node.id)
                mMessageClient.sendMessage(node.id, "/message", dataByteArray)
                Log.d("watchLogger", "msg sent")
            }
        }
    }
}
