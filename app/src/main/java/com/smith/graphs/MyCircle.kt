package com.smith.graphs

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.core.view.marginLeft
import androidx.core.view.marginStart
import androidx.core.view.marginTop

class MyCircle : View {

    private lateinit var path: Path
    private lateinit var circlePaint: Paint
    private lateinit var rectPaint: Paint
    private var initialX = 50.0f
    private var initialY = 50.0f

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


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
       // canvas?.drawPath(rectangle, circlePaint)
        canvas?.apply {

            drawRect(50.0f, 50.0f , width.toFloat() - 50, height.toFloat() - 50, rectPaint)
            drawCircle(initialX, initialY , 10.dp(), circlePaint)
        }
    }

    fun start() {
//        val valueAnimator = ValueAnimator()
//        valueAnimator.repeatCount = ValueAnimator.INFINITE
//        valueAnimator.addUpdateListener {
//            Log.d("salman", initialX.toString())
//            if (initialX > width) {
//                initialX = 50f
//            } else {
//                initialX += 50f
//            }
//            invalidate()
//        }
//        valueAnimator.duration = 20000
//        valueAnimator.start()
    }

    private fun init() {
        path = Path()
        path.addCircle(marginLeft + width/2.toFloat(), marginTop + height/2.toFloat(), 100.dp(), Path.Direction.CW)
        circlePaint = Paint()
        circlePaint.apply {
            style = Paint.Style.STROKE
            color = resources.getColor(R.color.colorPrimary)
            strokeWidth = 1.dp()
            strokeCap = Paint.Cap.ROUND
            pathEffect = CornerPathEffect(2.dp())
        }
        rectPaint = Paint()
        rectPaint.apply {
            isAntiAlias = true
            isFilterBitmap = true
            style = Paint.Style.STROKE
            strokeWidth = 4.dp()
            strokeCap = Paint.Cap.ROUND
            color = resources.getColor(R.color.dark)
            marginStart
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    private fun Int.dp(): Float {
        val metrics = Resources.getSystem().displayMetrics
        return this * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

}
