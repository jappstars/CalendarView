package com.jappstars.calendarview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class SquareTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Let width be determined by parent (GridLayout weight),
        // then make height = width so the view is a square.
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val w = measuredWidth
        // if width is zero (very rare) fall back to measuredHeight
        val size = if (w == 0) measuredHeight else w
        setMeasuredDimension(size, size)
    }
}
