package com.jappstars.calendarview

import android.graphics.Canvas
import android.graphics.Rect
import java.util.Calendar

/**
 * Implement to draw custom decorations for date cells.
 * The view will call draw on the canvas with the cell rect.
 */
interface DayDecorator {
    /**
     * Return true if decorator applies for this date.
     */
    fun shouldDecorate(day: Calendar): Boolean

    /**
     * Draw custom decorations inside the cell rect on canvas.
     */
    fun decorate(canvas: Canvas, cellRect: Rect, day: Calendar)
}
