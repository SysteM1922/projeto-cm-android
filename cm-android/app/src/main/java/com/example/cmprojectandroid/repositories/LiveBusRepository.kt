package com.example.cmprojectandroid.repositories

import com.example.cmprojectandroid.Model.Driver
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LiveBusRepository {
    private val databaseRef = FirebaseDatabase.getInstance().reference.child("drivers")

    // put driver
    fun getDrivers(): Flow<List<Driver>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val drivers = mutableListOf<Driver>()
                for (driverSnapshot in snapshot.children) {
                    val driver = driverSnapshot.getValue(Driver::class.java)
                    driver?.let {
                        drivers.add(it)
                    }
                }
                trySend(drivers).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error appropriately
                close(error.toException())
            }
        }

        databaseRef.addValueEventListener(listener)

        awaitClose {
            databaseRef.removeEventListener(listener)
        }
    }
}