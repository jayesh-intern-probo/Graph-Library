package com.example.finalgraphlibrary

class LineGraphPoint {
    ////////////////////////////////////////////////////////////////////////////////////////////////

    constructor(abscissa: Float, ordinate: Float) {
        this.abscissa = abscissa
        this.ordinate = ordinate
        toHighLight = false
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private var abscissa: Float = 0F
    private var ordinate: Float = 0F
    private var toHighLight: Boolean = false

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun getAbscissa(): Float = abscissa

    fun getOrdinate(): Float = ordinate

    fun setAbscissa(abscissa: Float){
        this.abscissa = abscissa
    }

    fun setOrdinate(ordinate: Float){
        this.ordinate = ordinate
    }

    fun highLight() {
        toHighLight = true
    }

    fun undoHighLight() {
        toHighLight = false
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}