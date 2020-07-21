package com.smith.graphs

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withScale
import androidx.core.graphics.withTranslation
import java.lang.Float.min
import kotlin.math.abs

class Graph : View {

    private lateinit var linePaint: Paint
    private lateinit var guidelinePaint: Paint
    private var mHeight = 0
    private var mWidth = 0
    private var touchX = -1f
    private var graphPath = Path()
    private var graphPoints = ArrayList<PointF>()
    private lateinit var onTouchListener: OnTouchListener

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        guidelinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        linePaint.apply {
            style = Paint.Style.STROKE
            color = resources.getColor(R.color.colorPrimary)
            strokeWidth = 1.dp()
            strokeCap = Paint.Cap.ROUND
            pathEffect = CornerPathEffect(2.dp())
        }
        guidelinePaint.apply {
            style = Paint.Style.STROKE
            color = resources.getColor(R.color.lightDark)
            strokeWidth = 1.dp()
            strokeCap = Paint.Cap.ROUND
            pathEffect = CornerPathEffect(8.dp())
        }
        initGraphPoints()
        graphPath = generateGraphPath()
    }

    private fun initCoordinates() {
        mWidth = width
        mHeight = height
    }


    private fun generateGraphPath(): Path {
        val path = Path()
        path.moveTo(graphPoints[0].x,graphPoints[0].y)
        for (i in graphPoints) {
            path.lineTo(i.x, i.y)
        }
        return path
    }

    private fun initGraphPoints() {
        for (i in 1..10) {
            val randomX = 10 * i
            val randomY = 100 * Math.random()
            graphPoints.add(PointF(randomX.toFloat(), randomY.toFloat()))
        }
    }

    // public view methods

    fun addPoint(y: Float) {
        graphPoints.add(PointF(100f, y))
        animateShiftGraph()
    }

    fun setOnTouchListener(onTouchListener: OnTouchListener) {
        this.onTouchListener = onTouchListener
    }

    // private view methods

    private fun animateShiftGraph() {
        ValueAnimator.ofInt(0, 10).run {
            addUpdateListener { animation ->
                shiftGraph(animation.animatedFraction)
                invalidate()
            }
            duration = 300
            start()
        }
    }

    private fun shiftGraph(animatedFraction: Float) {
        for (i in 0 until graphPoints.size) {
            graphPoints[i].x-=animatedFraction
        }
        if( graphPoints[1].x<0){
           graphPoints.removeAt(0)
        }
        graphPath = generateGraphPath()
    }

    private fun getSnap(scaledX: Float): Float {
        //snap to multiples of 10
        for (i in 1..100) {
            val diff = abs(scaledX - i * 10)
            if (diff < 10) {
                return i * 10f
            }
        }
        return scaledX
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        initCoordinates()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        val widthAvailable = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        val heightAvailable = MeasureSpec.getSize(heightMeasureSpec).toFloat()

        val desiredHeight = 200.dp()
        val desiredWidth = 200.dp()

        var resultHeight = 0f
        var resultWidth = 0f

        when(widthMode){
            MeasureSpec.AT_MOST ->{
                resultWidth = min(desiredWidth,widthAvailable)
            }
            MeasureSpec.EXACTLY ->{
                resultWidth = widthAvailable
            }
        }

        when(heightMode){
            MeasureSpec.AT_MOST ->{
                resultHeight = min(desiredHeight,heightAvailable)
            }
            MeasureSpec.EXACTLY ->{
                resultHeight = heightAvailable
            }
        }

        setMeasuredDimension(resultWidth.toInt(),resultHeight.toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.withPadding(24.dp(), 24.dp()) {
            canvas.scaleTo(100, 100) {
                linePaint.strokeWidth = 500f / width
                guidelinePaint.strokeWidth = 100f / width
                drawGuidelines(canvas)
                drawTouch(canvas)
                drawPath(graphPath, linePaint)
            }
        }
    }

    private fun drawTouch(canvas: Canvas) {
        if (touchX != -1f) {
            canvas.drawLine(touchX, 0f, touchX, 100f, guidelinePaint)

        }
    }

    private fun drawGuidelines(canvas: Canvas) {
        for (i in 0..10) {
            canvas.drawLine(0f, i * 10f, 100f, i * 10f, guidelinePaint)
        }
    }

    interface OnTouchListener {
        fun onTouch(x: Int, y: Int)
    }

    //Listeners

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val scaledX = (event!!.x / width) * 100
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = scaledX
            }
            MotionEvent.ACTION_MOVE -> {
                touchX = getSnap(scaledX)
                onTouchListener.onTouch(touchX.toInt(), -1)

            }
            MotionEvent.ACTION_UP -> {
                touchX = -1f
            }
        }
        invalidate()
        return true
    }

    //Helper functions

    private fun Int.dp(): Float {
        val metrics = Resources.getSystem().displayMetrics
        return this * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    private fun Canvas.scaleTo(x: Int, y: Int, block: Canvas.() -> Unit) {
        val xScale = width / x.toFloat()
        val yScale = height / y.toFloat()
        withTranslation(0f, height.toFloat()) {
            withScale(xScale, -yScale) {
                block()
            }
        }
    }

    private fun Canvas.withPadding(px: Float, py: Float, block: Canvas.() -> Unit) {
        val xScale = (width - 2 * px) / width
        val yScale = (height - 2 * px) / height
        withTranslation(px, py) {
            withScale(xScale, yScale) {
                block()
            }
        }
    }
}

