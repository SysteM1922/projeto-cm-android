package com.example.driverapp.Model

data class RealtimeLocation(
    var bus_id: String,
    var lat: Double? = null,
    var lng: Double? = null,
    var bus_name: String,
    var bus_color: String,
)