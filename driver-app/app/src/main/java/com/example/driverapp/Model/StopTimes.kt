package com.example.driverapp.Model

data class StopTime(
    val arrivalTime: String,
    val departureTime: String,
    val stopId: String,
    val stopSequence: Int,
    val tripId: String,
)