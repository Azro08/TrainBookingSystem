package com.example.trainbookingsystem.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils  {

    fun showDateTimePickerDialog(textView: TextView, context: Context) {
        val calendar = Calendar.getInstance()

        // Date picker dialog
        val datePickerDialog = DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, day)

                // Time picker dialog
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _: TimePicker, hourOfDay: Int, minute: Int ->
                        selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        selectedDate.set(Calendar.MINUTE, minute)

                        val formattedDateTime = formatDateTime(selectedDate.time)
                        textView.text = formattedDateTime
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )

                // Set minimum selectable date and time
                val now = Calendar.getInstance()
                if (now.after(selectedDate)) {
                    // If selected date is in the past, restrict selection to current time
                    timePickerDialog.updateTime(
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE)
                    )
                }

                timePickerDialog.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set minimum selectable date as the current date
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

        datePickerDialog.show()
    }


    private fun formatDateTime(dateTime: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(dateTime)
    }

    fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }

}