package com.wxy3265.vmeknowledge

interface Picture {
    fun onScale()
    fun onSizeChanged()
    fun doRotationEvent()
    fun doMoveEvent()
    fun onTouchEvent()
}