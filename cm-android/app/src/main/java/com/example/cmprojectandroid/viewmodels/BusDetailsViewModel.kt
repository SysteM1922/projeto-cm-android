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
                stop_id = "ROUS",
                stop_name = "Estação de Aveiro",
                stop_lat = 40.643771,
                stop_lon = -8.640994
            ),
            Stop(
                stop_id = "stop_id11_10_a",
                stop_name = "Universidade - Santiago A",
                stop_lat = 40.6298108,
                stop_lon = -8.6595775
            ),
            Stop(
                stop_id = "stop_id11_10_b",
                stop_name = "Universidade - Santiago B",
                stop_lat = 40.629946,
                stop_lon = -8.659104
            ),
            Stop(
                stop_id = "stop_id11_11",
                stop_name = "Universidade Crasto",
                stop_lat = 40.623036,
                stop_lon = -8.659233
            ),
            Stop(
                stop_id = "stop_id11_12",
                stop_name = "Av. 5 de Outubro",
                stop_lat = 40.641837,
                stop_lon = -8.646785
            )
        )
        _stops.value = mockStops
    }

    // Future integration: Fetch stops from the database based on busId
    fun fetchStopsForBus(busId: String) {
        // Implement actual data fetching logic here
    }
}
