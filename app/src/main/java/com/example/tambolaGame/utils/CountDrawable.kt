package com.example.tambolaGame.utils

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.tambolaGame.R
import kotlin.math.max


class CountDrawable(context: Context) : Drawable() {
    private val mBadgePaint: Paint
    private val mTextPaint: Paint
    private val mTxtRect: Rect = Rect()
    private var mCount = ""
    //private var mWillDraw = false

    override fun draw(canvas: Canvas) {
        /*if (!mWillDraw) {
            return
        }*/
        val bounds: Rect = bounds
        val width = (bounds.right - bounds.left).toFloat()
        val height = (bounds.bottom - bounds.top).toFloat()

        val radius = max(width, height) / 4
        val centerX = width - radius - 1 + 5
        val centerY = radius - 5
        when {
            mCount.length <= 2 -> canvas.drawCircle(centerX, centerY, (radius + 5.5).toFloat(), mBadgePaint)
            mCount.length == 3 -> canvas.drawCircle(centerX, centerY, (radius + 6.5).toFloat(), mBadgePaint)
            mCount.length == 4 -> canvas.drawCircle(centerX, centerY, (radius + 12.0).toFloat(), mBadgePaint)
            mCount.length == 5 -> canvas.drawCircle(centerX, centerY, (radius + 16.0).toFloat(), mBadgePaint)
            mCount.length > 5 -> canvas.drawCircle(centerX, centerY, (radius + 18.5).toFloat(), mBadgePaint)
        }

        // Draw badge count text inside the circle.
        mTextPaint.getTextBounds(mCount, 0, mCount.length, mTxtRect)
        val textHeight = (mTxtRect.bottom - mTxtRect.top).toFloat()
        val textY = centerY + textHeight / 2f
        if (mCount.length > 5) canvas.drawText(
            "99999+",
            centerX,
            textY,
            mTextPaint
        ) else canvas.drawText(mCount, centerX, textY, mTextPaint)
    }

    /*
    Sets the count (i.e notifications) to display.
     */
    fun setCount(count: String) {
        mCount = count
        // Only draw a badge if there are notifications.
        //mWillDraw = !count.equals("0", ignoreCase = true)
        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) { // do nothing
    }

    override fun setColorFilter(cf: ColorFilter?) { // do nothing
    }

    override fun getOpacity(): Int {
        return PixelFormat.UNKNOWN
    }

    init {
        val mTextSize: Float = context.resources.getDimension(R.dimen.badge_count_textsize)
        mBadgePaint = Paint()
        mBadgePaint.color = ContextCompat.getColor(
            context.applicationContext,
            R.color.background_color
        )
        mBadgePaint.isAntiAlias = true
        mBadgePaint.style = Paint.Style.FILL
        mTextPaint = Paint()
        mTextPaint.color = Color.BLACK
        mTextPaint.typeface = Typeface.DEFAULT
        mTextPaint.textSize = mTextSize
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER
    }
}