package com.example.driverapp.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverapp.Model.StopTime
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DriverViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var docRef: DocumentReference

    var actualTrip = ""
    var lastStop = 0
    private val _stopTimes = mutableStateListOf<StopTime>()
    val stopTimes: List<StopTime> get() = _stopTimes
    var currentPage by mutableIntStateOf(0)

    init {
        Log.d("DriverViewModel", "DriverViewModel created")
    }


    fun fetchDriverData(driverId: String?) {
        viewModelScope.launch {
            docRef = firestore.collection("drivers").document(driverId!!)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        if (document.data?.get("actualTrip") != null) {
                            actualTrip = document.data?.get("actualTrip").toString()
                            if (document.data?.get("lastStop") != null) {
                                lastStop = document.data?.get("lastStop").toString().toInt()
                            }
                        }
                    } else {
                        Log.d("DriverViewModel", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("DriverViewModel", "get failed with ", exception)
                }
        }
    }

    private fun fetchStops() {
        Log.d("DriverViewModel", "Fetching stops for trip_id: $actualTrip")
        viewModelScope.launch {
            val querySnapshot = firestore.collection("stop_times")
                .whereEqualTo("trip_id", actualTrip)
                .get()
                .await()

            Log.d("DriverViewModel", "Number of stops fetched: ${querySnapshot.size()}")
            for (document in querySnapshot) {
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
            currentPage = 1
            Log.d("DriverViewModel", "Stops fetched -> $stopTimes")
        }
    }

    fun isTripActive(): Boolean {
        return actualTrip != ""
    }

    fun navigateToPage(page: Int) {
        currentPage = page
    }

    fun startTrip(tripId: String) {
        viewModelScope.launch {
            actualTrip = tripId
            docRef.update("actualTrip", tripId)
                .addOnSuccessListener {
                    Log.d("DriverViewModel", "DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener { e ->
                    Log.w("DriverViewModel", "Error updating document", e)
                }
            fetchStops()
        }
    }

    fun endTrip() {
        this.actualTrip = ""
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
        navigateToPage(0)
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

    fun updateLastStop(lastStop: Int) {
        this.lastStop = lastStop
        docRef.update("lastStop", lastStop)
            .addOnSuccessListener {
                Log.d("DriverViewModel", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("DriverViewModel", "Error updating document", e)
            }
    }

    fun addToTravelHistory(cardId: String) {
        // DriverViewModel.addToTravelHistory(tripId)
    }
}