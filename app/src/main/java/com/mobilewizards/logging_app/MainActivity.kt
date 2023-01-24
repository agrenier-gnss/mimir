package com.mobilewizards.logging_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startButton = findViewById<Button>(R.id.startButton)

        startButton.setOnClickListener {
            GlobalScope.launch { var count = 0
                while (count<10) {
                    count++
                    Log.println(Log.INFO,"smt", count.toString())
                    yield()
                    try {
                        Thread.sleep(1000)
                    } catch (ex:Exception) {
                        Log.println(Log.ERROR,"smt",ex.message.toString())
                    }
                }
            }
        }
    }

    // Creates main_menu.xml
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.changeParameters -> {
                val setupIntent = Intent(this@MainActivity, SetupActivity::class.java)
                startActivity(setupIntent)
            }
        }
        return true
    }
}