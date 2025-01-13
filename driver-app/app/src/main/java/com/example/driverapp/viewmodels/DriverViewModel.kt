package com.example.driverapp.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.driverapp.Model.StopTime
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DriverViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var docRef: DocumentReference

    var actualTrip = ""
    var lastStop = 0
    private val _stopTimes = mutableStateListOf<StopTime>()
    val stopTimes: List<StopTime> get() = _stopTimes

    init {
        Log.d("DriverViewModel", "DriverViewModel created")
    }

    suspend fun fetchDriverData(driverId: String?): String {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                docRef = firestore.collection("drivers").document(driverId!!)
                docRef.get()
                    .addOnSuccessListener { document ->
                        Log.d("DriverViewModel", "DocumentSnapshot data: ${document.data}")
                        if (document != null) {
                            if (document.data?.get("actualTrip") != null) {
                                Log.d("DriverViewModel", "DocumentSnapshot data: ${document.data}")
                                actualTrip = document.data?.get("actualTrip").toString()
                                if (document.data?.get("lastStop") != null) {
                                    lastStop = document.data?.get("lastStop").toString().toInt()
                                }
                            }
                            Log.d("DriverViewModel", "Actual trip: $actualTrip")
                        } else {
                            Log.d("DriverViewModel", "No such document")
                        }
                        continuation.resume(actualTrip)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("DriverViewModel", "get failed with ", exception)
                        continuation.resumeWithException(exception)
                    }
            }
        }
    }

    private suspend fun fetchStops() {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                Log.d("DriverViewModel", "Fetching stops for trip_id: $actualTrip")
                firestore.collection("stop_times")
                    .whereEqualTo("trip_id", actualTrip)
                    .get()
                    .addOnSuccessListener { documents ->
                        Log.d("DriverViewModel", "Number of stops fetched: ${documents.size()}")
                        for (document in documents) {
                            _stopTimes.add(
                                StopTime(
                                    document.data["arrival_time"].toString(),
                                    document.data["departure_time"].toString(),
                                    document.data["stop_id"].toString(),
                                    document.data["stop_sequence"].toString().toInt(),
                                    document.data["trip_id"].toString(),
                                )
                            )
                        }
                        _stopTimes.sortBy { it.stopSequence }
                        continuation.resume(Unit)
                        Log.d("DriverViewModel", "Stops fetched -> $stopTimes")
                    }
                    .addOnFailureListener { exception ->
                        Log.d("DriverViewModel", "Error getting documents: ", exception)
                        continuation.resumeWithException(exception)
                    }
            }
        }
    }

    suspend fun startTrip(tripId: String) {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine<Void?> { continuation ->
                Log.d("DriverViewModel", "Starting trip: $tripId")
                actualTrip = tripId
                docRef.update("actualTrip", tripId)
                    .addOnSuccessListener {
                        Log.d("DriverViewModel", "DocumentSnapshot successfully updated!")
                        continuation.resume(null)
                    }
                    .addOnFailureListener { e ->
                        Log.w("DriverViewModel", "Error updating document", e)
                        continuation.resumeWithException(e)
                    }
                docRef.update("lastStop", 0)
                    .addOnSuccessListener {
                        Log.d("DriverViewModel", "DocumentSnapshot successfully updated! end")
                    }
                    .addOnFailureListener { e ->
                        Log.w("DriverViewModel", "Error updating document", e)
                    }
            }
            fetchStops()
        }
    }

    fun endTrip() {
        this.actualTrip = ""
        // clear stop times
        _stopTimes.clear()
        this.lastStop = 0
        docRef.update("actualTrip", null)
            .addOnSuccessListener {
                Log.d("DriverViewModel", "DocumentSnapshot successfully updated! end")
            }
            .addOnFailureListener { e ->
                Log.w("DriverViewModel", "Error updating document", e)
            }
        docRef.update("lastStop", null)
            .addOnSuccessListener {
                Log.d("DriverViewModel", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("DriverViewModel", "Error updating document", e)
            }
    }

    fun validateCard(cardId: String): String {
        // verify if cardID is in users collection
        var result = ""
        firestore.collection("users")
            .whereEqualTo("card_id", cardId)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("DriverViewModel", "Card not found")
                } else {
                    Log.d("DriverViewModel", "Card found")
                    result = documents.first().data["user_name"].toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.d("DriverViewModel", "Error getting documents: ", exception)
            }
        return result
    }

    fun updateLastStop() {
        this.lastStop++
        docRef.update("lastStop", lastStop)
            .addOnSuccessListener {
                Log.d("DriverViewModel", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("DriverViewModel", "Error updating document", e)
            }
    }

    suspend fun getCurrentStopName(): String {

        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                var stopName = ""
                Log.d(
                    "DriverViewModel",
                    "Fetching stop name for stop_id: ${stopTimes[lastStop].stopId}"
                )
                firestore.collection("stops")
                    .whereEqualTo("stop_id", stopTimes[lastStop].stopId)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            Log.d("DriverViewModel", "Stop not found")
                        } else {
                            Log.d("DriverViewModel", "Stop found")
                            stopName = documents.first().data["stop_name"].toString()
                        }
                        continuation.resume(stopName)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("DriverViewModel", "Error getting documents: ", exception)
                        continuation.resumeWithException(exception)
                    }
            }
        }
    }

    fun getCurrentStopArrivalTime(): String {
        return stopTimes[lastStop].arrivalTime
    }

    fun addToTravelHistory(cardId: String) {
        // DriverViewModel.addToTravelHistory(tripId)
    }
}