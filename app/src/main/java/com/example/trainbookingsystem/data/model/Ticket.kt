package com.example.trainbookingsystem.data.model

data class Ticket(
    val id : String = "",
    val startDestination : String = "",
    val endDestination : String = "",
    val trainNum : Int = 0,
    val freeSeats : List<Int> = emptyList(),
    val price : Double = 0.0,
    val departureTime : String = "",
    val arrivalTime : String = "",
)