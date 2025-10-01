package com.jappstars.calendarview

import java.util.Calendar

enum class SelectionMode { NONE, SINGLE, MULTIPLE, RANGE }


fun interface OnDateSelectedListener {
    fun onDateSelected(selected: List<Calendar>)
}

fun interface OnMonthChangedListener {
    fun onMonthChanged(year: Int, month: Int)
}