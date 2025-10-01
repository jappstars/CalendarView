package com.jappstars.calendarview

import java.util.Calendar

internal fun startOfMonth(calendar: Calendar): Calendar {
    val c = calendar.clone() as Calendar
    c.set(Calendar.DAY_OF_MONTH, 1)
    c.set(Calendar.HOUR_OF_DAY, 0)
    c.set(Calendar.MINUTE, 0)
    c.set(Calendar.SECOND, 0)
    c.set(Calendar.MILLISECOND, 0)
    return c
}

internal fun endOfMonth(calendar: Calendar): Calendar {
    val c = startOfMonth(calendar)
    c.add(Calendar.MONTH, 1)
    c.add(Calendar.DAY_OF_MONTH, -1)
    return c
}

internal fun sameDay(a: Calendar, b: Calendar): Boolean {
    return a.get(Calendar.YEAR) == b.get(Calendar.YEAR)
            && a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
}
