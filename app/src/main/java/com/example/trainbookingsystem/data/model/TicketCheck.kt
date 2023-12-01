package com.example.trainbookingsystem.data.model

data class TicketCheck (
    val id : String = "",
    val userId : String = "",
    val ticket: Ticket = Ticket(),
    val date : String = "",
    val price : Double = 0.0,
    val paymentType : String = "",
    val cardDetails : CardDetails = CardDetails(),
    val seatNumber : Int = 0
)