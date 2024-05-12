package com.example.trainbookingsystem.data.model

import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.abs

data class Ticket(
    val id: String = "",
    val startDestination: String = "",
    val endDestination: String = "",
    val trainNum: Int = 0,
    val freeSeats: List<Int> = emptyList(),
    val price: List<Double> = emptyList(),
    val departureTime: String = "",
    val arrivalTime: String = "",
)

fun List<Double>.priceListToString(): String {
    return this.joinToString(", ") { "%.1fруб".format(it) }
}

fun Ticket.getTripDuration(): Int {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val departureDateTime = dateFormat.parse(departureTime)
    val arrivalDateTime = dateFormat.parse(arrivalTime)

    val departureTimeMillis = departureDateTime?.time ?: 0
    val arrivalTimeMillis = arrivalDateTime?.time ?: 0

    val durationMillis = abs(arrivalTimeMillis - departureTimeMillis)

    return (durationMillis / (1000 * 60 * 60)).toInt()
}
