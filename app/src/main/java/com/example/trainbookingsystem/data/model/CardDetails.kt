package com.example.trainbookingsystem.data.model

data class CardDetails(
    val cardNumber : Int = 0,
    val holderName : String = "",
    val cvv : Int = 0,
    val expireDate : String = ""
)
