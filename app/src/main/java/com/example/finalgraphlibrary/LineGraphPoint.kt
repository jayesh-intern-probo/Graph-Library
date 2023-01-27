package com.example.finalgraphlibrary

import android.graphics.Path
import kotlin.math.abs

class LineGraphPoint {
    ////////////////////////////////////////////////////////////////////////////////////////////////

    constructor(abscissa: Float, ordinate: Float) {
        this.abscissa = abscissa
        this.ordinate = ordinate
        this.probability = abscissa
        this.toHighLight = false
        this.information = ""
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private var abscissa: Float = 0F
    private var ordinate: Float = 0F
    private var toHighLight: Boolean = false
    private var probability: Float = 0F
    private var information: String = ""

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun getAbscissa(): Float = abscissa

    fun getOrdinate(): Float = ordinate

    fun getInformation(): String = information

    fun getProbability(): Float = probability

    fun toHighLight(): Boolean = toHighLight

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun setAbscissa(abscissa: Float) {
        this.abscissa = abscissa
    }

    fun setOrdinate(ordinate: Float) {
        this.ordinate = ordinate
    }

    fun setInformation(information: String){
        this.information = information
    }

    fun setProbability(probability: Float) {
        this.probability = probability
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun highLight() {
        toHighLight = true
    }

    fun undoHighLight() {
        toHighLight = false
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}