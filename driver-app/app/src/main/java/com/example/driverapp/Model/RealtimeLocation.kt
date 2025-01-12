package com.example.driverapp.Model

data class RealtimeLocation(
    var lat: Double = 0.0,
    var lng: Double = 0.0,
)

data class InitRealTimeLocation(
    var bus_id: String = "",
    var bus_name: String = "",
    var bus_color: String = "",
)