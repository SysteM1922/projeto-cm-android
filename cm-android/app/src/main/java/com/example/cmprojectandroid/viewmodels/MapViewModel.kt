package com.example.cmprojectandroid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {
    private val _isMapLoaded = mutableStateOf(false)
    val isMapLoaded: State<Boolean> = _isMapLoaded

    private val _cameraPosition = mutableStateOf(
        CameraPosition.builder()
            .target(DEFAULT_LOCATION)
            .zoom(DEFAULT_ZOOM)
            .build()
    )
    val cameraPosition: State<CameraPosition> = _cameraPosition

    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> = _selectedLocation

    private val _isFavoritesSelected = mutableStateOf(false)
    val isFavoritesSelected: State<Boolean> = _isFavoritesSelected

    fun setFavoritesFilter(selected: Boolean) {
        _isFavoritesSelected.value = selected
    }

    fun setMapLoaded(loaded: Boolean) {
        _isMapLoaded.value = loaded
    }

    fun updateCameraPosition(position: CameraPosition) {
        viewModelScope.launch {
            _cameraPosition.value = position
        }
    }

    fun setSelectedLocation(location: LatLng) {
        viewModelScope.launch {
            _selectedLocation.value = location
        }
    }

    companion object {
        private val DEFAULT_LOCATION = LatLng(40.643771, -8.640994)
        private const val DEFAULT_ZOOM = 13f
    }
}