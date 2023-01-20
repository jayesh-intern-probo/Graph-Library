package com.example.finalgraphlibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val lineGraph: LineGraph = findViewById(R.id.myGraph)

        val xLabels: List<String> = listOf("9:35 pm", "9:40 pm", "9:45 pm")
        val yLabels: List<String> = listOf("10", "20", "30", "40", "50", "60", "70", "80", "90", "100")

        lineGraph.setUpXAxisLabels(xLabels)
        lineGraph.setUpYAxisLabels(yLabels)
    }
}