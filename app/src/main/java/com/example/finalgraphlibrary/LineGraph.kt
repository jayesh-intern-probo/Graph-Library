package com.example.finalgraphlibrary

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller

class LineGraph: View {
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    constructor(context: Context): super(context)

    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.LineGraph, 0, 0
        ).apply {
            try {
                graphLeftPadding =
                    getDimensionPixelSize(R.styleable.LineGraph_graphLeftPadding, 0).toFloat()
                graphRightPadding =
                    getDimensionPixelSize(R.styleable.LineGraph_graphRightPadding, 0).toFloat()
                graphTopPadding =
                    getDimensionPixelSize(R.styleable.LineGraph_graphTopPadding, 0).toFloat()
                graphBottomPadding =
                    getDimensionPixelSize(R.styleable.LineGraph_graphBottomPadding, 0).toFloat()
                axesThickness =
                    getDimensionPixelSize(R.styleable.LineGraph_axesThickness, 1).toFloat()
                labelTextSize =
                    getDimensionPixelSize(R.styleable.LineGraph_labelSize, 0).toFloat()
                xAxisLabelMargin =
                    getDimensionPixelSize(R.styleable.LineGraph_labelMarginFromXAxis, 0).toFloat()
                yAxisLabelMargin =
                    getDimensionPixelSize(R.styleable.LineGraph_labelMarginFromYAxis, 0).toFloat()
                yAxisTitleReserveSize =
                    getDimensionPixelSize(R.styleable.LineGraph_reserveSizeForYAxisTitle, 0).toFloat()
                yAxisLabelTitle =
                    getString(R.styleable.LineGraph_yAxisTitle).toString()
                curvePaint.strokeWidth =
                    getDimensionPixelSize(R.styleable.LineGraph_plotLineWidth, 4).toFloat()
                markedPointRadius =
                    getDimensionPixelSize(R.styleable.LineGraph_markerCircleRadius, 8).toFloat()
            } finally {
                recycle()
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private var graphLeftPadding: Float = 0F
    private var graphRightPadding: Float = 0F
    private var graphTopPadding: Float = 0F
    private var graphBottomPadding: Float = 0F
    private var graphWidth: Float = 0F
    private var graphHeight: Float = 0F
    private var graphXOrigin: Float = 0F
    private var graphYOrigin: Float = 0F
    private lateinit var graphData: LineGraphData

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private var canvasWidth: Float = 0F
    private var canvasHeight: Float = 0F

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private var axesThickness: Float = 0F
    private var labelTextSize: Float = 0F
    private var xAxisLabelMargin: Float = 0F
    private var yAxisLabelMargin: Float = 0F
    private var yAxisTitleReserveSize: Float = 0F
    private var yAxisLabelTitle: String = ""

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private var canAxesBeDrawn: Boolean = false
    private var canXAxisLabelBeDrawn: Boolean = false
    private var canYAxisLabelBeDrawn: Boolean = false
    private var areDataPointsSet: Boolean = false
    private var canDottedLineBeDrawn: Boolean = false

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val axesPaint: Paint = Paint()
    private val labelPaint: Paint = Paint()
    private val curvePaint: Paint = Paint()
    private val dottedLinePaint: Paint = Paint()
    private val markedPointPaintPrimary: Paint = Paint()
    private val markedPointPaintSecondary: Paint = Paint()
    private val rippleEffectPrimaryPaint: Paint = Paint()
    private val rippleEffectSecondaryPaint: Paint = Paint()

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val xAxisLabelList: MutableList<String> = mutableListOf()
    private val yAxisLabelList: MutableList<String> = mutableListOf()

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val plotLinePath: Path = Path()

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private lateinit var touchArea: RectF
    private var touchedIndex: Int = -1
    private var markedPointRadius: Float = 0F
    private var ripplePointRadius: Float = 0F

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun initializeValues() {
        graphHeight = canvasHeight - graphTopPadding - graphBottomPadding
        graphWidth = canvasWidth - graphLeftPadding - graphRightPadding
        graphXOrigin = graphLeftPadding
        graphYOrigin = canvasHeight - graphBottomPadding
        touchArea = RectF(graphXOrigin, graphYOrigin - graphHeight, graphXOrigin + graphWidth, graphYOrigin)
        canAxesBeDrawn = true
        setUpRipplePaint()
    }

    private fun setUpRipplePaint() {
        rippleEffectPrimaryPaint.style = Paint.Style.FILL_AND_STROKE
        rippleEffectPrimaryPaint.color = Color.parseColor("#BAD7FF")
        rippleEffectSecondaryPaint.style = Paint.Style.FILL_AND_STROKE
        rippleEffectSecondaryPaint.color = Color.parseColor("#197BFF")
    }

    private fun setUpLabelPaint(paint: Paint) {
        paint.color = Color.parseColor("#757575")
        paint.textSize = labelTextSize
        paint.textAlign = Paint.Align.RIGHT
    }

    private fun setUpDottedLinePaint(paint: Paint) {
        paint.color = Color.BLACK
        paint.strokeWidth = axesThickness
        paint.style = Paint.Style.STROKE
        paint.pathEffect = DashPathEffect(floatArrayOf(10F, 10F), 0F)
    }

    private fun setUpCurvePaint(paint: Paint) {
        //TODO Implementation Specific
        var colorStart: String = "#5EA3FF"
        var colorEnd: String = "#197BFF"
        if(graphData.graphPointList[graphData.getCountPoints()-1].getOrdinate() > (graphYOrigin - (graphHeight-yAxisTitleReserveSize)/2)) {
            colorStart = "#F2ADA5"
            rippleEffectPrimaryPaint.color = Color.parseColor("#F2ADA5")
            colorEnd = "#E7685A"
            rippleEffectSecondaryPaint.color = Color.parseColor("#E7685A")
        }

        val linearGradient: LinearGradient = LinearGradient(
            graphXOrigin, graphYOrigin,
            graphXOrigin + graphWidth, graphYOrigin - graphHeight,
            Color.parseColor(colorStart), Color.parseColor(colorEnd),
            Shader.TileMode.MIRROR)
        paint.shader = linearGradient;
        paint.style = Paint.Style.STROKE
    }

    private fun setUpMarkedPointPaint() {
        markedPointPaintPrimary.color = Color.BLACK
        markedPointPaintPrimary.style = Paint.Style.FILL_AND_STROKE
        markedPointPaintSecondary.color = Color.WHITE
        markedPointPaintSecondary.style = Paint.Style.FILL_AND_STROKE
    }


    fun setUpXAxisLabels(list: List<String>) {
        xAxisLabelList.clear()
        xAxisLabelList.addAll(0, list)
        canXAxisLabelBeDrawn = true
    }

    fun setUpYAxisLabels(list: List<String>) {
        yAxisLabelList.clear()
        yAxisLabelList.addAll(0, list)
        canYAxisLabelBeDrawn = true
    }

    fun setUpData(data: List<Pair<Float, Float>>) {
        graphData = LineGraphData(data)
        areDataPointsSet = true
    }

    fun setUpMaxXValue(value: Float) = graphData.setUpMaxAbscissa(value)
    fun setUpMinXValue(value: Float) = graphData.setUpMinAbscissa(value)
    fun setUpMaxYValue(value: Float) = graphData.setUpMaxOrdinate(value)
    fun setUpMinYValue(value: Float) = graphData.setUpMinOrdinate(value)

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun drawAxes(canvas: Canvas?, paint: Paint) {
        paint.color = Color.parseColor("#E3E3E3")
        paint.strokeWidth = axesThickness
        canvas?.drawLine(graphXOrigin, graphYOrigin, graphXOrigin + graphWidth, graphYOrigin, paint)
        canvas?.drawLine(graphXOrigin, graphYOrigin, graphXOrigin, graphYOrigin - graphHeight, paint)

//        paint.color = Color.BLUE
//        canvas?.drawCircle(graphXOrigin, graphYOrigin, 5f, paint)
//        canvas?.drawCircle(graphXOrigin + graphWidth, graphYOrigin, 5f, paint)
//        canvas?.drawCircle(graphXOrigin, graphYOrigin - graphHeight, 5f, paint)
//        canvas?.drawCircle(graphXOrigin + graphWidth, graphYOrigin - graphHeight, 5f, paint)
    }

    private fun drawXAxisLabels(canvas: Canvas?, paint: Paint) {
        setUpLabelPaint(paint)
        //TODO Implementation Specific
        var yPosition: Float = graphYOrigin + yAxisLabelMargin
        val part: Float = graphWidth / (xAxisLabelList.size - 1)
        val bounds: Rect = Rect()

        for(i in 0 until xAxisLabelList.size) {
            val label: String = xAxisLabelList[i]
            val widthRemaining = graphXOrigin + (i * part)
            labelPaint.getTextBounds(label, 0, label.length, bounds)

            val xPosition = when(i) {
                0 -> graphXOrigin
                xAxisLabelList.size - 1 -> graphXOrigin + graphWidth - bounds.width().toFloat()
                else -> widthRemaining - bounds.width().toFloat() / 2
            }

//            canvas?.drawPoint(xPosition , yPosition, axesPaint)
            canvas?.drawText(label, xPosition + bounds.right, yPosition - bounds.top, labelPaint)
        }
    }

    private fun drawYAxisLabels(canvas: Canvas?, paint: Paint) {
        setUpLabelPaint(paint)
        //TODO Implementation Specific
        val height: Float = graphHeight - yAxisTitleReserveSize
        val part: Float = height / yAxisLabelList.size
        val bounds: Rect = Rect()

        for(i in 0 until yAxisLabelList.size) {
            val label: String = yAxisLabelList[i]
            labelPaint.getTextBounds(label, 0, label.length, bounds)

            val xPosition = graphXOrigin - bounds.left - yAxisLabelMargin
            val yPosition = graphYOrigin - ((i+1) * part)

//            canvas?.drawPoint(xPosition, yPosition, axesPaint)
            canvas?.drawText(label, xPosition, yPosition - bounds.top / 2, paint)
        }

        labelPaint.getTextBounds(yAxisLabelTitle, 0, yAxisLabelTitle.length, bounds)
        val xPosition =  graphXOrigin - bounds.left - yAxisLabelMargin
        val yPosition = graphYOrigin - graphHeight
        canvas?.drawText(yAxisLabelTitle, xPosition, yPosition - bounds.top, labelPaint)
    }

    private fun plotGraphData(canvas: Canvas?, paint: Paint) {
        setUpCurvePaint(paint)
        plotLinePath.reset()
        if(!graphData.isInitialized())
            graphData.initializeList(graphWidth, graphHeight - yAxisTitleReserveSize, graphXOrigin, graphYOrigin)

        plotLinePath.moveTo(graphData.graphPointList[0].getAbscissa(), graphData.graphPointList[0].getOrdinate())

        for(i in 1 until graphData.getCountPoints()) {
            val controlX = (graphData.graphPointList[i-1].getAbscissa() + graphData.graphPointList[i].getAbscissa()) / 2
            val controlY = graphData.graphPointList[i-1].getOrdinate()
            val pointY = graphData.graphPointList[i].getOrdinate()

            plotLinePath.cubicTo(
                controlX,
                controlY,
                controlX,
                pointY,
                graphData.graphPointList[i].getAbscissa(),
                graphData.graphPointList[i].getOrdinate()
            )
        }
        setUpCurvePaint(paint)
        canvas?.drawPath(plotLinePath, paint)
    }

    private fun drawDottedLine(canvas: Canvas?, paint: Paint) {
        setUpDottedLinePaint(paint)
        setUpMarkedPointPaint()
        if(touchedIndex >= 0 && touchedIndex < graphData.getCountPoints()) {
            val xPosition: Float = graphData.graphPointList[touchedIndex].getAbscissa()
            val yPosition: Float = graphData.graphPointList[touchedIndex].getOrdinate()
            canvas?.drawLine(xPosition, yPosition, xPosition, graphYOrigin - graphHeight, paint)

            if(touchedIndex != graphData.getCountPoints() - 1) {
                canvas?.drawCircle(xPosition, yPosition, markedPointRadius, markedPointPaintPrimary)
                canvas?.drawCircle(xPosition, yPosition, markedPointRadius / 2, markedPointPaintSecondary)
            }
        }
    }

    private fun mockRippleEffect(canvas: Canvas?) {
        if(ripplePointRadius < markedPointRadius)
            ripplePointRadius += 0.1F
        else
            ripplePointRadius = 0F

        val xPosition: Float = graphData.graphPointList[graphData.getCountPoints()-1].getAbscissa()
        val yPosition: Float = graphData.graphPointList[graphData.getCountPoints()-1].getOrdinate()

        canvas?.drawCircle(xPosition, yPosition, markedPointRadius, rippleEffectPrimaryPaint)
        canvas?.drawCircle(xPosition, yPosition, ripplePointRadius, rippleEffectSecondaryPaint)

        invalidate()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        canvasWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        canvasHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        initializeValues()

        if(canAxesBeDrawn)
            drawAxes(canvas, axesPaint)
        if(canXAxisLabelBeDrawn)
            drawXAxisLabels(canvas, labelPaint)
        if(canYAxisLabelBeDrawn)
            drawYAxisLabels(canvas, labelPaint)
        if(areDataPointsSet) {
            plotGraphData(canvas, curvePaint)
            mockRippleEffect(canvas)
        }
        if(canDottedLineBeDrawn)
            drawDottedLine(canvas, dottedLinePaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val xTouchLocation: Float? = event?.x
        val yTouchLocation: Float? = event?.y

        when(event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if(touchArea.contains(xTouchLocation!!, yTouchLocation!!)) {
                    for(i in 1 until graphData.getCountPoints()) {
                        if(xTouchLocation <= graphData.graphPointList[i].getAbscissa()) {
                            canDottedLineBeDrawn = true
                            touchedIndex = i
                            return  true
                        }
                    }
                }

                canDottedLineBeDrawn = false
            }

            MotionEvent.ACTION_MOVE -> {
                if(touchArea.contains(xTouchLocation!!, yTouchLocation!!)) {
                    for(i in 1 until graphData.getCountPoints()) {
                        if(xTouchLocation <= graphData.graphPointList[i].getAbscissa()) {
                            canDottedLineBeDrawn = true
                            touchedIndex = i
                            invalidate()
                            return  true
                        }
                    }
                }

                canDottedLineBeDrawn = false
            }

            MotionEvent.ACTION_UP -> {
                canDottedLineBeDrawn = false
                invalidate()
            }
        }

        return true
    }
}