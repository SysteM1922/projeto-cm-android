package com.example.driverapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.driverapp.Model.RealtimeLocation
import com.example.driverapp.repositories.LocationRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LocationViewModel(
    private val repository: LocationRepo = LocationRepo()
) : ViewModel() {

    private val _locationState = MutableStateFlow(RealtimeLocation())
    val locationState = _locationState.asStateFlow()

    init {
        // Observe changes from Realtime Database
        viewModelScope.launch {
            repository.getLocationFlow().collect { newLocation ->
                _locationState.value = newLocation
            }
        }
    }

    /**
     * Public function to update the location in Realtime DB.
     */
    fun updateLocation(lat: Double, lng: Double) {
        val newLocation = RealtimeLocation(
            lat = lat,
            lng = lng,
            timestamp = System.currentTimeMillis()
        )
        viewModelScope.launch {
            repository.updateLocation(newLocation)
        }
    }
}
