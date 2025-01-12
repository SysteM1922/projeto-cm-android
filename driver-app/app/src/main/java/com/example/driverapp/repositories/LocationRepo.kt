package com.example.driverapp.repositories

import com.example.driverapp.Model.RealtimeLocation
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocationRepo {

    private val databaseRef = FirebaseDatabase.getInstance()
        .reference
        .child("drivers")
        .child("bus_xpto")

    fun getLocationFlow(): Flow<RealtimeLocation> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Option 1: Read as a RealtimeLocation object
                val location = snapshot.getValue(RealtimeLocation::class.java)
                if (location != null) {
                    trySend(location).isSuccess
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        databaseRef.addValueEventListener(listener)
        awaitClose { databaseRef.removeEventListener(listener) }

    }

    suspend fun updateLocation(newLocation: RealtimeLocation) {
        databaseRef.setValue(newLocation)
    }

}