package com.example.finalgraphlibrary

import android.content.Context
import android.hardware.camera2.params.MandatoryStreamCombination.MandatoryStreamInformation
import android.icu.text.Transliterator.Position
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginLeft
import java.lang.String.format

class PopUp: ConstraintLayout {
    ////////////////////////////////////////////////////////////////////////////////////////////////

    constructor(context: Context): super(context)
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet)

    init {
        LayoutInflater.from(context).inflate(R.layout.merge, this, true)
        textPrompt = findViewById<ConstraintLayout>(R.id.clTextPrompt)
        probability = findViewById<TextView>(R.id.tvProbability)
        time = findViewById<TextView>(R.id.tvTime)
        information = findViewById<TextView>(R.id.tvHeadline)
        downTriangle = findViewById<ImageView>(R.id.downTriangle)

        textPrompt.visibility = GONE
        information.visibility = GONE
        downTriangle.visibility = GONE
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private var textPrompt: ConstraintLayout
    private var probability: TextView
    private var time: TextView
    private var information: TextView
    private var downTriangle: ImageView

    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun hidePopUp() {
        this.textPrompt.visibility = GONE
        this.downTriangle.visibility = GONE
    }

    fun setUpPopUp(probability: Float, time: String, information: String = "") {
        this.textPrompt.visibility = VISIBLE
        this.downTriangle.visibility = VISIBLE
        setUpProbability(format("%.1f", probability).toString())
        setUpTime(time)
        setUpInformation(information)
    }

    fun setUpLocation(xPosition: Float) {
        val layoutParams: ConstraintLayout.LayoutParams = textPrompt.layoutParams as LayoutParams
        Log.i("Layout", "${layoutParams.matchConstraintMaxWidth}")
        layoutParams.leftMargin = (xPosition - layoutParams.width/2f).toInt()

        textPrompt.layoutParams = layoutParams
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private fun setUpProbability(probability: String) {
        this.probability.text = "$probability%"
    }

    private fun setUpTime(time: String) {
        this.time.text = "$time"
    }

    private fun setUpInformation(information: String) {
        if(information.isEmpty())
            this.information.visibility = GONE
        else {
            this.information.visibility = VISIBLE
            this.information.text = "$information"
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
}