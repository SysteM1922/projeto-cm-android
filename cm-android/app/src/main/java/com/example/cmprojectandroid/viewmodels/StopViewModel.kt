package com.example.cmprojectandroid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmprojectandroid.Model.Bus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class StopViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    // Backing property for buses
    private val _buses = MutableStateFlow<List<Bus>>(emptyList())
    val buses: StateFlow<List<Bus>> = _buses

    // State for stop existence
    private val _stopExists = MutableStateFlow<Boolean?>(null) // `null` means loading state
    val stopExists: StateFlow<Boolean?> = _stopExists

    fun fetchBusesForStop(stopId: String) {
        viewModelScope.launch {
            try {
                // Step 1: Query stop_times collection by stop_id
                val stopTimesSnapshot = firestore.collection("stop_times")
                    .whereEqualTo("stop_id", stopId)
                    .get()
                    .await()

                // Extract trip IDs and departure times from stop_times
                val tripIdToDepartureTime = stopTimesSnapshot.documents.mapNotNull { doc ->
                    val tripId = doc.getString("trip_id")
                    val departureTime = doc.getString("departure_time")
                    if (tripId != null && departureTime != null) {
                        tripId to departureTime
                    } else null
                }.toMap()

                if (tripIdToDepartureTime.isEmpty()) {
                    _buses.value = emptyList()
                    return@launch
                }

                // Step 2: Query trips collection by trip_ids
                val tripsSnapshot = firestore.collection("trips")
                    .whereIn("trip_id", tripIdToDepartureTime.keys.toList())
                    .get()
                    .await()

                // Map trip documents to buses, including the arrival time
                val buses = tripsSnapshot.documents.mapNotNull { doc ->
                    val tripId = doc.getString("trip_id")
                    val busName = doc.getString("trip_short_name")
                    val departureTime = tripIdToDepartureTime[tripId]
                    if (tripId != null && busName != null && departureTime != null) {
                        Bus(
                            busId = tripId,
                            busName = busName,
                            departureTime = departureTime
                        )
                    } else null
                }

                // Update the state flow with fetched buses
                _buses.value = buses
            } catch (e: Exception) {
                e.printStackTrace()
                _buses.value = emptyList() // Handle errors gracefully
            }
        }
    }
}