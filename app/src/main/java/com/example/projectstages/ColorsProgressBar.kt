package com.example.projectstages

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat

/*
From tutorial:
https://www.androiddevelopersolutions.com/2015/01/android-custom-horizontal-progress-bar.html
https://github.com/mukesh4u/CustomProgressBar
*/
class ColorsProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
//    ,
//    progressItemsList: ArrayList<ProgressItem>
//) : AppCompatSeekBar(context, attrs, defStyle) {
) : AppCompatSeekBar(context, attrs, defStyle) {

    private var mProgressItemsList = emptyList<ProgressItem>()

    fun initData(progressItemsList: ArrayList<ProgressItem>) {
        mProgressItemsList = progressItemsList
    }

    fun initData(completedTasks: Int, inProgressTasks: Int, inThoughtTasks: Int) {

    }

    @Synchronized
    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDraw(canvas: Canvas) {
        if (mProgressItemsList.size > 0) {
            val progressBarWidth = width
            val progressBarHeight = height
            val thumboffset = thumbOffset
            var lastProgressX = 0
            var progressItemWidth: Int
            var progressItemRight: Int
            for (i in 0 until mProgressItemsList.size) {
                val progressItem: ProgressItem = mProgressItemsList!![i]
                val progressPaint = Paint()
                progressPaint.color = ContextCompat.getColor(context, progressItem.color)
                progressItemWidth = (progressItem.progressItemPercentage
                        * progressBarWidth / 100).toInt()
                progressItemRight = lastProgressX + progressItemWidth

                // for last item give right to progress item to the width
                if (i == mProgressItemsList.size - 1
                    && progressItemRight != progressBarWidth
                ) {
                    progressItemRight = progressBarWidth
                }
                val progressRect = Rect()
                progressRect.set(
                    lastProgressX, thumboffset / 2,
                    progressItemRight, progressBarHeight - thumboffset / 2
                )
                canvas.drawRect(progressRect, progressPaint)
                lastProgressX = progressItemRight
            }
            super.onDraw(canvas)
        }
    }
}

class ProgressItem(
    val color: Int,
    val progressItemPercentage: Float
)