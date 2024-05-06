package com.example.trainbookingsystem.util

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import com.example.trainbookingsystem.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object Constants {
    const val SHARED_PREF_NAME = "user_pref"
    const val USER_KEY = "user_key"
    const val ROLE_KEY = "role_key"
    const val ADMIN = "admin"
    const val USER = "user"
    const val TICKET_ID = "ticket_id"
    const val USER_ID = "user_id"
    const val EMAIL = "email"
    const val LANGUAGE_KEY = "language_key"

    fun generateRandomId(): String {
        val characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val random =
            Random(System.currentTimeMillis()) // Seed the random number generator with the current time

        val randomString = StringBuilder(28)

        for (i in 0 until 28) {
            val randomIndex = random.nextInt(characters.length)
            randomString.append(characters[randomIndex])
        }

        return randomString.toString()
    }

    fun getMainCities(context: Context) : List<String>{
        return listOf(
            context.getString(R.string.minsk),
            context.getString(R.string.grodno),
            context.getString(R.string.vitebsk),
            context.getString(R.string.gomel),
            context.getString(R.string.mogilev),
            context.getString(R.string.brest),
            context.getString(R.string.moscow),
            context.getString(R.string.saint_petersburg),
        )
    }

    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis() / 1000 // Get current timestamp in seconds
    }


}