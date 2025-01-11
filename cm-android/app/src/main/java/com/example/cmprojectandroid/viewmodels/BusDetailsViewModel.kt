package com.example.cmprojectandroid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmprojectandroid.Model.Stop
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BusDetailsViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    // Backing property for stops
    private val _stops = MutableStateFlow<List<Stop>>(emptyList())
    val stops: StateFlow<List<Stop>> = _stops

    fun fetchStopsForBus(busId: String) {
        viewModelScope.launch {
            try {
                // Step 1: Query stop_times to get stop_ids for the given trip_id (busId)
                val stopTimesSnapshot = firestore.collection("stop_times")
                    .whereEqualTo("trip_id", busId)
                    .get()
                    .await()

                val stopIds = stopTimesSnapshot.documents.mapNotNull { it.getString("stop_id") }

                if (stopIds.isEmpty()) {
                    _stops.value = emptyList()
                    return@launch
                }

                // Step 2: Query stops collection to get details for the fetched stop_ids
                val stopsSnapshot = firestore.collection("stops")
                    .whereIn("stop_id", stopIds)
                    .get()
                    .await()

                // Map stopsSnapshot documents to Stop objects
                val stops = stopsSnapshot.documents.mapNotNull { doc ->
                    val stopId = doc.getString("stop_id")
                    val stopName = doc.getString("stop_name")
                    val stopLat = doc.getDouble("stop_lat")
                    val stopLon = doc.getDouble("stop_lon")

                    if (stopId != null && stopName != null && stopLat != null && stopLon != null) {
                        Stop(
                            stop_id = stopId,
                            stop_name = stopName,
                            stop_lat = stopLat,
                            stop_lon = stopLon
                        )
                    } else null
                }

                // Update the state flow with fetched stops
                _stops.value = stops
            } catch (e: Exception) {
                e.printStackTrace()
                _stops.value = emptyList() // Handle errors gracefully
            }
        }
    }
}
