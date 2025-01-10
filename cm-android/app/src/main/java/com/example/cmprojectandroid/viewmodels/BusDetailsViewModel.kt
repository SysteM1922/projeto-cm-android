package com.example.cmprojectandroid.viewmodels

import androidx.lifecycle.ViewModel
import com.example.cmprojectandroid.Model.Stop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class BusDetailsViewModel : ViewModel() {

    // Backing property for stops
    private val _stops = MutableStateFlow<List<Stop>>(emptyList())
    val stops: StateFlow<List<Stop>> = _stops

    init {
        // Load mock stops
        loadMockStops()
    }

    private fun loadMockStops() {
        val mockStops = listOf(
            Stop(
                id = "stop_id11_1",
                name = "Estação de Aveiro",
                latitude = 40.643771,
                longitude = -8.640994
            ),
            Stop(
                id = "stop_id11_10_a",
                name = "Universidade - Santiago A",
                latitude = 40.6298108,
                longitude = -8.6595775
            ),
            Stop(
                id = "stop_id11_10_b",
                name = "Universidade - Santiago B",
                latitude = 40.629946,
                longitude = -8.659104
            ),
            Stop(
                id = "stop_id11_11",
                name = "Universidade Crasto",
                latitude = 40.623036,
                longitude = -8.659233
            ),
            Stop(
                id = "stop_id11_12",
                name = "Av. 5 de Outubro",
                latitude = 40.641837,
                longitude = -8.646785
            )
        )
        _stops.value = mockStops
    }

    // Future integration: Fetch stops from the database based on busId
    fun fetchStopsForBus(busId: String) {
        // Implement actual data fetching logic here
    }
}
