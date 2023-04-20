package com.mobilewizards.logging_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TableLayout

class SurveyHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_surveyhistory)

        // Get the parent view to add the TableLayout to
        val parentView = findViewById<ViewGroup>(R.id.container_layout)

        val surveyList = arrayOf("a", "b")

        //create a layout for each survey that has been made
        for(x in surveyList) {

            // Inflate the layout file that contains the TableLayout
            val tableLayout = layoutInflater.inflate(R.layout.layout_presets, parentView, false).findViewById<TableLayout>(R.id.sensorSquarePreset)



            // Remove the tableLayout's parent, if it has one
            (tableLayout.parent as? ViewGroup)?.removeView(tableLayout)

            // Add the TableLayout to the parent view
            parentView.addView(tableLayout)

        }


    }
}