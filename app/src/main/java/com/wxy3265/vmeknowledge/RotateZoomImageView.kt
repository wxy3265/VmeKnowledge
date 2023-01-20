package com.wxy3265.vmeknowledge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Context
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import kotlin.math.atan


abstract class RotateZoomImageView(context: Context, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatImageView(context, attrs) ,Picture{

    private val TAG = "RotateZoomImageView"
    private var mScaleGestureDetector: ScaleGestureDetector

    private var mImageMatrix: Matrix = Matrix()
    private val savedMatrix: Matrix = Matrix()

    private var x = 0
    private var y = 0

    private var mLastAngle = 0

    // 第一个按下的手指的点
    private val startPoint = PointF()


    private val mScaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            //缩放比例因子
            val scaleFactor = detector.scaleFactor
            mImageMatrix.postScale(scaleFactor, scaleFactor, x.toFloat(), y.toFloat())
            imageMatrix = mImageMatrix

            return true
        }
    }

    init {
        mScaleGestureDetector = ScaleGestureDetector(context, mScaleListener)
        scaleType = ScaleType.MATRIX
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w != oldw || h != oldh) {
            val transX = (w - drawable.intrinsicWidth) / 2.0
            val transY = (h - drawable.intrinsicHeight) / 2.0
            mImageMatrix.setTranslate(transX.toFloat(), transY.toFloat())
            imageMatrix = mImageMatrix
            x = w / 2
            y = h / 2
        }
    }


    private fun doRotationEvent(ev: MotionEvent): Boolean {
        //计算两个手指的角度
        val dx = ev.getX(0) - ev.getX(1)
        val dy = ev.getY(0) - ev.getY(1)
        //弧度
        val radians = atan(dy.toDouble() / dx.toDouble())
        //角度
        val degrees = (radians * 180 / Math.PI).toInt()

        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_POINTER_UP -> mLastAngle =
                degrees
            MotionEvent.ACTION_MOVE -> {
                when {
                    (degrees - mLastAngle) > 45 -> mImageMatrix.postRotate(
                        -5f,
                        x.toFloat(),
                        y.toFloat()
                    )
                    (degrees - mLastAngle) < -45 -> mImageMatrix.postRotate(
                        5f,
                        x.toFloat(),
                        y.toFloat()
                    )
                    else -> mImageMatrix.postRotate(
                        (degrees - mLastAngle).toFloat(),
                        x.toFloat(),
                        y.toFloat()
                    )
                }
                imageMatrix = mImageMatrix
                mLastAngle = degrees
            }
        }

        return true
    }

    private fun doMoveEvent(ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                mImageMatrix.set(savedMatrix)
                mImageMatrix.postTranslate(ev.x - startPoint.x, ev.y - startPoint.y)
                imageMatrix = mImageMatrix
            }
        }
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            Log.d("drag", "onTouch: x= ${event.rawX.toInt()},y=${event.rawY.toInt()}" )
            mImageMatrix.set(imageMatrix)
            savedMatrix.set(mImageMatrix)
            startPoint[event.x] = event.y
            return true
        }
        return when (event.pointerCount) {
            3 -> mScaleGestureDetector.onTouchEvent(event)
            2 -> doRotationEvent(event)
            1 -> doMoveEvent(event)
            else -> super.onTouchEvent(event)
        }
    }
}

