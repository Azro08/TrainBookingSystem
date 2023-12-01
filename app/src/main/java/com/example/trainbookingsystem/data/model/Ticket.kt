package com.example.trainbookingsystem.data.model

data class Ticket(
    val id : String = "",
    val startDestination : String = "",
    val endDestination : String = "",
    val trainNum : Int = 0,
    val freeSeats : List<Int> = emptyList(),
    val price : List<Double> = emptyList(),
    val departureTime : String = "",
    val arrivalTime : String = "",
)

fun List<Double>.priceListToString() : String{
    return this.joinToString(", ") { "%.1fруб".format(it) }
}