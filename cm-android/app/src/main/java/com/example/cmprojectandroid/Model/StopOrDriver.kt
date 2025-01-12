package com.example.cmprojectandroid.Model

sealed class StopOrDriver(val name: String, val id: String) {
    data class Stop(val stopId: String, val stopName: String) : StopOrDriver(stopId, stopName)
    data class Driver(val driverId: String, val driverName: String) : StopOrDriver(driverId, driverName)
}