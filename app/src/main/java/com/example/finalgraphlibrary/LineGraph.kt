package com.example.finalgraphlibrary

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

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

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val axesPaint: Paint = Paint()
    private val labelPaint: Paint = Paint()
    private val curvePaint: Paint = Paint()

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val xAxisLabelList: MutableList<String> = mutableListOf()
    private val yAxisLabelList: MutableList<String> = mutableListOf()

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private val plotLinePath: Path = Path()

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun initializeValues() {
        graphHeight = canvasHeight - graphTopPadding - graphBottomPadding
        graphWidth = canvasWidth - graphLeftPadding - graphRightPadding
        graphXOrigin = graphLeftPadding
        graphYOrigin = canvasHeight - graphBottomPadding
        canAxesBeDrawn = true
    }

    private fun setUpLabelPaint(paint: Paint) {
        paint.color = Color.parseColor("#757575")
        paint.textSize = labelTextSize
        paint.textAlign = Paint.Align.RIGHT
    }

    private fun setUpCurvePaint(paint: Paint) {
        //TODO Implementation Specific
        var colorStart: String = "#5EA3FF"
        var colorEnd: String = "#197BFF"
        if(graphData.graphPointList[graphData.getCountPoints()-1].getOrdinate() > (graphYOrigin - (graphHeight-yAxisTitleReserveSize)/2)) {
            colorStart = "#F2ADA5"
            colorEnd = "#E7685A"
        }

        val linearGradient: LinearGradient = LinearGradient(
            graphXOrigin, graphYOrigin,
            graphXOrigin + graphWidth, graphYOrigin - graphHeight,
            Color.parseColor(colorStart), Color.parseColor(colorEnd),
            Shader.TileMode.MIRROR)
        paint.shader = linearGradient;
        paint.style = Paint.Style.STROKE
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
            Log.i("Points", "${graphData.graphPointList[i].getAbscissa()}, ${graphData.graphPointList[i].getOrdinate()}")
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
        if(areDataPointsSet)
            plotGraphData(canvas, curvePaint)
    }
}