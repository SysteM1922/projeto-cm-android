package com.example.cmprojectandroid.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MapViewModel : ViewModel() {
    // Camera position state
    private val _cameraPosition = MutableStateFlow(
        CameraPosition.fromLatLngZoom(LatLng(40.643771, -8.640994), 12f)
    )
    val cameraPosition: StateFlow<CameraPosition> = _cameraPosition

    fun updateCameraPosition(newPosition: CameraPosition) {
        _cameraPosition.value = newPosition
    }

    // Selected Stop
    var selectedStopIdInitially: String = ""

    // Search and Filter States
    var searchQuery by mutableStateOf("")
    var filterOption by mutableStateOf("All")
}
