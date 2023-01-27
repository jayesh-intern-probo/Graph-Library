package com.example.finalgraphlibrary

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.Random

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val popUp: PopUp = findViewById(R.id.myPopUp)
        val lineGraph: LineGraph = findViewById(R.id.myGraph)
        val xLabels: List<String> = listOf("9:35 pm", "9:40 pm", "9:45 pm", "9:50 pm", "9:55 pm")
        val yLabels: List<String> = listOf("10", "20", "30", "40", "50", "60", "70", "80", "90", "100")

        lineGraph.setUpXAxisLabels(xLabels)
        lineGraph.setUpYAxisLabels(yLabels)
        lineGraph.assignPopUp(popUp)

        var data: MutableList<Pair<Float, Float>> = mutableListOf()
        for(i in 1..15) {
            data.add(Pair(3 * i.toFloat(), Random().nextFloat() * 100))
        }

        lineGraph.setUpData(data)
        lineGraph.setUpMaxYValue(100F)
        lineGraph.setUpMinYValue(0F)
    }
}