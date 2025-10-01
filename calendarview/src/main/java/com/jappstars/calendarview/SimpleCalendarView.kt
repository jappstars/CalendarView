package com.jappstars.calendarview

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jappstars.calendarview.databinding.ScvLayoutBinding
import java.text.DateFormatSymbols
import java.util.Calendar

data class CalendarDay(
    val year: Int, val month: Int, // 1-based (Jan = 1, Dec = 12)
    val day: Int
) {
    companion object {
        fun from(year: Int, month: Int, day: Int): CalendarDay {
            return CalendarDay(year, month, day)
        }

        fun from(calendar: Calendar): CalendarDay {
            return CalendarDay(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1, // Calendar.MONTH is 0-based
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }

        fun today(): CalendarDay {
            return from(Calendar.getInstance())
        }
    }

    fun toCalendar(): Calendar {
        return Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1) // convert back to 0-based
            set(Calendar.DAY_OF_MONTH, day)
        }
    }
}

class SimpleCalendarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val binding: ScvLayoutBinding =
        ScvLayoutBinding.inflate(LayoutInflater.from(context), this)

    private var currentCalendar: Calendar = Calendar.getInstance()

    // Selected dates (support multiple)
    private val selectedDates: MutableList<CalendarDay> = mutableListOf()

    // Selection mode
    var selectionMode: SelectionMode = SelectionMode.SINGLE

    // Attribute values
    private var headerBackground: Drawable? = null
    private var headerTextColor: Int = Color.BLACK
    private var headerTextStyle: Int = 0
    private var headerHeight: Int = dpToPx(48)

    private var prevIconRes: Int = R.drawable.ic_before_24
    private var nextIconRes: Int = R.drawable.ic_next_24
    private var pickerIconRes: Int = R.drawable.ic_arrow_drop_down_24
    private var iconBackground: Drawable? = null

    private var weekdayTextColor: Int = Color.DKGRAY
    private var weekdayTextStyle: Int = 0
    private var weekdayBackground: Drawable? = null

    private var dayTextColor: Int = Color.BLACK
    private var dayTextStyle: Int = 0
    private var dayBackground: Drawable? = null

    private var selectedDayColor: Int = Color.WHITE
    private var selectedDayBackground: Drawable? = null

    private var todayTextColor: Int = Color.RED
    private var todayBackground: Drawable? = null

    private var showOtherMonthDays: Boolean = true
    private var otherMonthDayTextColor: Int = Color.GRAY
    private var otherMonthDayBackground: Drawable? = null

    private var calendarBackground: Drawable? = null

    private var firstDayOfWeek: Int = Calendar.SUNDAY
    private var weekdayLabelMode: WeekdayLabelMode = WeekdayLabelMode.ONE_LETTER

    // Listeners
    private var onDateSelectedListener: OnDateSelectedListener? = null
    private var onMonthChangedListener: OnMonthChangedListener? = null

    init {
        orientation = VERTICAL
        initAttributes(attrs)
        applyBackgrounds()
        setupHeader()
        setupWeekdays()
        populateDays()
    }

    private fun initAttributes(attrs: AttributeSet?) {
        if (attrs == null) return
        val a = context.obtainStyledAttributes(attrs, R.styleable.SimpleCalendarView)

        calendarBackground = a.getDrawable(R.styleable.SimpleCalendarView_scv_background)

        headerBackground = a.getDrawable(R.styleable.SimpleCalendarView_scv_headerBackground)
        headerTextColor =
            a.getColor(R.styleable.SimpleCalendarView_scv_headerTextColor, headerTextColor)
        headerTextStyle = a.getResourceId(R.styleable.SimpleCalendarView_scv_headerTextStyle, 0)
        headerHeight =
            a.getDimensionPixelSize(R.styleable.SimpleCalendarView_scv_headerHeight, headerHeight)

        prevIconRes = a.getResourceId(R.styleable.SimpleCalendarView_scv_prevIcon, prevIconRes)
        nextIconRes = a.getResourceId(R.styleable.SimpleCalendarView_scv_nextIcon, nextIconRes)
        pickerIconRes =
            a.getResourceId(R.styleable.SimpleCalendarView_scv_pickerIcon, pickerIconRes)
        iconBackground = a.getDrawable(R.styleable.SimpleCalendarView_scv_iconBackground)

        weekdayTextColor =
            a.getColor(R.styleable.SimpleCalendarView_scv_weekdayTextColor, weekdayTextColor)
        weekdayTextStyle = a.getResourceId(R.styleable.SimpleCalendarView_scv_weekdayTextStyle, 0)
        weekdayBackground = a.getDrawable(R.styleable.SimpleCalendarView_scv_weekdayBackground)

        dayTextColor = a.getColor(R.styleable.SimpleCalendarView_scv_dayTextColor, dayTextColor)
        dayTextStyle = a.getResourceId(R.styleable.SimpleCalendarView_scv_dayTextStyle, 0)
        dayBackground = a.getDrawable(R.styleable.SimpleCalendarView_scv_dayBackground)

        selectedDayColor =
            a.getColor(R.styleable.SimpleCalendarView_scv_selectedDayColor, selectedDayColor)
        selectedDayBackground =
            a.getDrawable(R.styleable.SimpleCalendarView_scv_selectedDayBackground)

        todayTextColor =
            a.getColor(R.styleable.SimpleCalendarView_scv_todayTextColor, todayTextColor)
        todayBackground = a.getDrawable(R.styleable.SimpleCalendarView_scv_todayBackground)

        showOtherMonthDays =
            a.getBoolean(R.styleable.SimpleCalendarView_scv_showOtherMonthDays, showOtherMonthDays)
        otherMonthDayTextColor = a.getColor(
            R.styleable.SimpleCalendarView_scv_otherMonthDayTextColor, otherMonthDayTextColor
        )
        otherMonthDayBackground =
            a.getDrawable(R.styleable.SimpleCalendarView_scv_otherMonthDayBackground)

        firstDayOfWeek =
            a.getInt(R.styleable.SimpleCalendarView_scv_firstDayOfWeek, Calendar.SUNDAY)
        val mode = a.getInt(R.styleable.SimpleCalendarView_scv_weekdayLabelMode, 0)
        weekdayLabelMode = when (mode) {
            1 -> WeekdayLabelMode.THREE_LETTER
            2 -> WeekdayLabelMode.FULL
            else -> WeekdayLabelMode.ONE_LETTER
        }

        a.recycle()
    }

    private fun applyBackgrounds() {
        background = calendarBackground
    }

    private fun setupHeader() {
        binding.scvHeader.background = headerBackground
        binding.scvMonthYear.setTextColor(headerTextColor)
        if (headerTextStyle != 0) binding.scvMonthYear.setTextAppearance(context, headerTextStyle)
        binding.scvHeader.layoutParams.height = headerHeight

        binding.scvPrev.setImageResource(prevIconRes)
        binding.scvNext.setImageResource(nextIconRes)
        binding.scvPicker.setImageResource(pickerIconRes)
        binding.scvPrev.background = iconBackground
        binding.scvNext.background = iconBackground
        binding.scvPicker.background = iconBackground

        updateMonthYearTitle()

        binding.scvPrev.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, -1)
            updateMonthYearTitle()
            populateDays()
            onMonthChangedListener?.onMonthChanged(currentCalendar)
        }

        binding.scvNext.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, 1)
            updateMonthYearTitle()
            populateDays()
            onMonthChangedListener?.onMonthChanged(currentCalendar)
        }

        binding.scvPicker.setOnClickListener {
            val dialog = MonthYearPickerDialog(
                initialMonth = currentCalendar.get(Calendar.MONTH),
                initialYear = currentCalendar.get(Calendar.YEAR)
            ) { month, year ->
                currentCalendar.set(Calendar.MONTH, month)
                currentCalendar.set(Calendar.YEAR, year)
                updateMonthYearTitle()
                populateDays()
                onMonthChangedListener?.onMonthChanged(currentCalendar)
            }
            dialog.show((context as AppCompatActivity).supportFragmentManager, "MonthYearPicker")
        }
    }

    private fun setupWeekdays() {
        binding.scvWeekdays.removeAllViews()
        val weekdays = getOrderedWeekdays()

        weekdays.forEach { day ->
            val tv = TextView(context).apply {
                text = formatWeekday(day)
                setTextColor(weekdayTextColor)
                if (weekdayTextStyle != 0) setTextAppearance(context, weekdayTextStyle)
                background = weekdayBackground
                layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f)
                textAlignment = View.TEXT_ALIGNMENT_CENTER
            }
            binding.scvWeekdays.addView(tv)
        }
    }

    /*private fun populateDays() {
        binding.gridDays.removeAllViews()

        val tempCal = currentCalendar.clone() as Calendar
        tempCal.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfMonth = tempCal.get(Calendar.DAY_OF_WEEK)
        val offset = (7 + (firstDayOfMonth - firstDayOfWeek)) % 7
        tempCal.add(Calendar.DAY_OF_MONTH, -offset)

        for (i in 0 until 42) {
            val dayCal = tempCal.clone() as Calendar
            val isCurrentMonth = dayCal.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)

            val tv = SquareTextView(context).apply {
                text = dayCal.get(Calendar.DAY_OF_MONTH).toString()
                gravity = Gravity.CENTER
                includeFontPadding = false
                textAlignment = TEXT_ALIGNMENT_CENTER

                val calDay = CalendarDay.from(dayCal)

                when {
                    selectedDates.contains(calDay) -> {
                        if (selectedDayBackground != null) background = selectedDayBackground
                        setTextColor(selectedDayColor)
                    }
                    isToday(dayCal) -> {
                        if (todayBackground != null) background = todayBackground
                        setTextColor(todayTextColor)
                    }
                    isCurrentMonth -> {
                        setTextColor(dayTextColor)
                        if (dayTextStyle != 0) setTextAppearance(context, dayTextStyle)
                        if (dayBackground != null) background = dayBackground
                    }
                    else -> {
                        if (showOtherMonthDays) {
                            setTextColor(otherMonthDayTextColor)
                            if (otherMonthDayBackground != null) background = otherMonthDayBackground
                        } else {
                            text = ""
                        }
                    }
                }

                setOnClickListener {
                    when (selectionMode) {
                        SelectionMode.SINGLE -> {
                            selectedDates.clear()
                            selectedDates.add(calDay)
                        }
                        SelectionMode.MULTIPLE -> {
                            if (selectedDates.contains(calDay)) {
                                selectedDates.remove(calDay)
                            } else {
                                selectedDates.add(calDay)
                            }
                        }
                    }
                    populateDays()
                    onDateSelectedListener?.onDateSelected(calDay, selectedDates.toList())
                }
            }

            val lp = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2))
            }
            binding.gridDays.addView(tv, lp)
            tempCal.add(Calendar.DAY_OF_MONTH, 1)
        }
    }*/

    private fun populateDays() {
        binding.gridDays.removeAllViews()

        val tempCal = currentCalendar.clone() as Calendar
        tempCal.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfMonth = tempCal.get(Calendar.DAY_OF_WEEK)
        val offset = (7 + (firstDayOfMonth - firstDayOfWeek)) % 7

        // Start from previous month to fill the first week
        tempCal.add(Calendar.DAY_OF_MONTH, -offset)

        val daysInMonth = currentCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val totalCells = offset + daysInMonth
        // Calculate extra cells to complete last row
        val rows = Math.ceil(totalCells / 7.0).toInt()
        val totalGridCells = rows * 7

        for (i in 0 until totalGridCells) {
            val dayCal = tempCal.clone() as Calendar
            val isCurrentMonth = dayCal.get(Calendar.MONTH) == currentCalendar.get(Calendar.MONTH)

            val tv = SquareTextView(context).apply {
                text = dayCal.get(Calendar.DAY_OF_MONTH).toString()
                gravity = Gravity.CENTER
                includeFontPadding = false
                textAlignment = TEXT_ALIGNMENT_CENTER

                val calDay = CalendarDay.from(dayCal)

                when {
                    selectedDates.contains(calDay) -> {
                        if (selectedDayBackground != null) background = selectedDayBackground
                        setTextColor(selectedDayColor)
                    }

                    isToday(dayCal) -> {
                        if (todayBackground != null) background = todayBackground
                        setTextColor(todayTextColor)
                    }

                    isCurrentMonth -> {
                        setTextColor(dayTextColor)
                        if (dayTextStyle != 0) setTextAppearance(context, dayTextStyle)
                        if (dayBackground != null) background = dayBackground
                    }

                    else -> {
                        if (showOtherMonthDays) {
                            setTextColor(otherMonthDayTextColor)
                            if (otherMonthDayBackground != null) background =
                                otherMonthDayBackground
                        } else {
                            text = ""
                            visibility = View.INVISIBLE
                        }
                    }
                }

                setOnClickListener {
                    if (!isCurrentMonth) return@setOnClickListener
                    when (selectionMode) {
                        SelectionMode.SINGLE -> {
                            selectedDates.clear()
                            selectedDates.add(calDay)
                        }

                        SelectionMode.MULTIPLE -> {
                            if (selectedDates.contains(calDay)) selectedDates.remove(calDay)
                            else selectedDates.add(calDay)
                        }
                    }
                    populateDays()
                    onDateSelectedListener?.onDateSelected(calDay, selectedDates.toList())
                }
            }

            val lp = GridLayout.LayoutParams().apply {
                width = 0
                height = GridLayout.LayoutParams.WRAP_CONTENT
                columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2))
            }
            binding.gridDays.addView(tv, lp)
            tempCal.add(Calendar.DAY_OF_MONTH, 1)
        }
    }


    private fun updateMonthYearTitle() {
        val monthYear = DateFormat.format("MMMM yyyy", currentCalendar.time)
        binding.scvMonthYear.text = monthYear
    }

    private fun getOrderedWeekdays(): List<Int> {
        val days = (Calendar.SUNDAY..Calendar.SATURDAY).toList()
        val startIndex = days.indexOf(firstDayOfWeek)
        return days.drop(startIndex) + days.take(startIndex)
    }

    private fun formatWeekday(day: Int): String {
        val symbols = DateFormatSymbols.getInstance()
        return when (weekdayLabelMode) {
            WeekdayLabelMode.ONE_LETTER -> symbols.shortWeekdays[day].first().toString()
            WeekdayLabelMode.THREE_LETTER -> symbols.shortWeekdays[day]
            WeekdayLabelMode.FULL -> symbols.weekdays[day]
        }
    }

    private fun isToday(cal: Calendar): Boolean {
        val today = Calendar.getInstance()
        return today.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && today.get(Calendar.DAY_OF_YEAR) == cal.get(
            Calendar.DAY_OF_YEAR
        )
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    // --- Public API ---
    fun setSelectedDates(dates: List<CalendarDay>) {
        selectedDates.clear()
        selectedDates.addAll(dates)
        populateDays()
    }

    fun getSelectedDates(): List<CalendarDay> = selectedDates.toList()

    fun setOnDateSelectedListener(listener: OnDateSelectedListener) {
        this.onDateSelectedListener = listener
    }

    fun setOnMonthChangedListener(listener: OnMonthChangedListener) {
        this.onMonthChangedListener = listener
    }

    enum class WeekdayLabelMode {
        ONE_LETTER, THREE_LETTER, FULL
    }

    enum class SelectionMode {
        SINGLE, MULTIPLE
    }

    fun interface OnDateSelectedListener {
        fun onDateSelected(date: CalendarDay, selectedDates: List<CalendarDay>)
    }

    fun interface OnMonthChangedListener {
        fun onMonthChanged(calendar: Calendar)
    }
}
