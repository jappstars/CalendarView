package com.jappstars.calendarview

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Locale

class MonthYearPickerDialog(
    private val initialMonth: Int,
    private val initialYear: Int,
    private val minYear: Int = 1900,
    private val maxYear: Int = 2100,
    private val listener: (month: Int, year: Int) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_month_year_picker, null)

        val monthPicker = view.findViewById<NumberPicker>(R.id.monthPicker)
        val yearPicker = view.findViewById<NumberPicker>(R.id.yearPicker)

        // Find the custom buttons
        val btnOk = view.findViewById<Button>(R.id.btnOk)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        // Month names setup (as before)
        val months = (0..11).map {
            Calendar.getInstance().apply { set(Calendar.MONTH, it) }
                .getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())!!
        }.toTypedArray()

        monthPicker.minValue = 0
        monthPicker.maxValue = 11
        monthPicker.displayedValues = months
        monthPicker.value = initialMonth

        yearPicker.minValue = minYear
        yearPicker.maxValue = maxYear
        yearPicker.value = initialYear

        // Handle OK button click
        btnOk.setOnClickListener {
            listener(monthPicker.value, yearPicker.value)
            dismiss() // Dismiss the dialog after successful action
        }

        // Handle Cancel button click
        btnCancel.setOnClickListener {
            dismiss() // Just dismiss the dialog
        }

        // Create the AlertDialog using a theme that removes its default elements
        // (This assumes you define CustomMonthYearPickerDialogTheme in styles.xml)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomMonthYearPickerDialogTheme)
            .setView(view) // Pass the fully custom view
            // !!! DO NOT CALL setPositiveButton or setNegativeButton !!!
            .create()

        // Critical step: Remove the default dialog window background color
        // so that the background set on your root view is visible.
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        return dialog
    }
}