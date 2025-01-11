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
                val stopSnapshot = firestore.collection("stops")
                    .whereEqualTo("stop_id", stopId)
                    .get()
                    .await()

                if (stopSnapshot.isEmpty) {
                    _stopExists.value = false
                } else {
                    _stopExists.value = true
                    // Fetch buses only if the stop exists
                    val stopTimesSnapshot = firestore.collection("stop_times")
                        .whereEqualTo("stop_id", stopId)
                        .get()
                        .await()

                    val tripIds = stopTimesSnapshot.documents.mapNotNull { it.getString("trip_id") }
                    if (tripIds.isEmpty()) {
                        _buses.value = emptyList()
                    } else {
                        val tripsSnapshot = firestore.collection("trips")
                            .whereIn("trip_id", tripIds)
                            .get()
                            .await()

                        val buses = tripsSnapshot.documents.mapNotNull { doc ->
                            val tripId = doc.getString("trip_id")
                            val busName = doc.getString("trip_short_name")
                            val arrivalTime = stopTimesSnapshot.documents.find {
                                it.getString("trip_id") == tripId
                            }?.getString("departure_time")

                            if (tripId != null && busName != null && arrivalTime != null) {
                                Bus(busId = tripId, busName = busName, arrivalTime = arrivalTime)
                            } else null
                        }
                        _buses.value = buses
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _stopExists.value = false // Handle failure gracefully
            }
        }
    }
}