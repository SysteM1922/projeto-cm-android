package com.example.driverapp.repositories

import com.example.driverapp.Model.RealtimeLocation
import com.google.firebase.database.*

class LocationRepo {

    private var databaseRef = FirebaseDatabase.getInstance()
        .reference
        .child("error")
        .child("busId")

    fun changeBusId(newBusId: String) {
        databaseRef = FirebaseDatabase.getInstance()
            .reference
            .child("drivers")
            .child(newBusId)
    }

    fun updateLocation(newLocation: RealtimeLocation) {
        databaseRef.setValue(newLocation)
    }

    fun deleteTrip() {
        databaseRef.removeValue()
    }
}