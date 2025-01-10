package com.example.cmprojectandroid.viewmodels

import androidx.lifecycle.ViewModel
import com.example.cmprojectandroid.Model.Bus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StopViewModel : ViewModel() {

    // Backing property for buses
    private val _buses = MutableStateFlow<List<Bus>>(emptyList())
    val buses: StateFlow<List<Bus>> = _buses

    init {
        // Mock data initialization
        loadMockBuses()
    }

    private fun loadMockBuses() {
        val mockBuses = listOf(
            Bus(busId = "bus_1", busName = "Bus 11", arrivalTime = "08:15 AM"),
            Bus(busId = "bus_2", busName = "Bus 5 (Santiago)", arrivalTime = "08:20 AM"),
            Bus(busId = "bus_3", busName = "Bus 5 (Solposto)", arrivalTime = "08:25 AM")
            // Add more mock buses as needed
        )
        _buses.value = mockBuses
    }

    // Function to simulate updating buses (for future integration)
    fun updateBuses(newBuses: List<Bus>) {
        _buses.value = newBuses
    }
}