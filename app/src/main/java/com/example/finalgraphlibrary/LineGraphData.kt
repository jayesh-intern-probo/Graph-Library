package com.example.finalgraphlibrary

import android.util.Log
import kotlin.math.max
import kotlin.math.min

class LineGraphData(private val list: List<Pair<Float, Float>>) {

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    var graphPointList: MutableList<LineGraphPoint> = mutableListOf()
    private var countPoints: Int = list.size
    private var minGraphPoint: LineGraphPoint = LineGraphPoint(Float.MAX_VALUE, Float.MAX_VALUE)
    private var maxGraphPoint: LineGraphPoint = LineGraphPoint(Float.MIN_VALUE, Float.MIN_VALUE)
    private var isInitialized: Boolean = false

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    init {
        for(pair in list) {
            maxGraphPoint.setAbscissa(max(maxGraphPoint.getAbscissa(), pair.first))
            maxGraphPoint.setOrdinate(max(maxGraphPoint.getOrdinate(), pair.second))
            minGraphPoint.setAbscissa(min(minGraphPoint.getAbscissa(), pair.first))
            minGraphPoint.setOrdinate(min(minGraphPoint.getOrdinate(), pair.second))
            graphPointList.add(LineGraphPoint(pair.first, pair.second))
        }
    }

    fun initializeList(graphWidth: Float, graphHeight: Float, graphXOrigin: Float, graphYOrigin: Float) {
        isInitialized = true
        graphPointList.clear()
        val unitX: Float = graphWidth / (maxGraphPoint.getAbscissa() - minGraphPoint.getAbscissa())
        val unitY: Float = graphHeight / (maxGraphPoint.getOrdinate() - minGraphPoint.getOrdinate())
        for(pair in list) {
            val abscissa: Float = graphXOrigin + ((pair.first - minGraphPoint.getAbscissa()) * unitX)
            val ordinate: Float = graphYOrigin - ((pair.second - minGraphPoint.getOrdinate()) * unitY)

            graphPointList.add(LineGraphPoint(abscissa, ordinate))
            graphPointList[graphPointList.size - 1].setProbability(pair.second)
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getCountPoints(): Int = countPoints

    fun setUpMaxAbscissa(abscissa: Float) {
        maxGraphPoint.setAbscissa(abscissa)
    }

    fun setUpMinAbscissa(abscissa: Float) {
        minGraphPoint.setAbscissa(abscissa)
    }

    fun setUpMaxOrdinate(ordinate: Float) {
        maxGraphPoint.setOrdinate(ordinate)
    }

    fun setUpMinOrdinate(ordinate: Float) {
        minGraphPoint.setOrdinate(ordinate)
    }

    fun isInitialized(): Boolean = isInitialized

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}