package com.example.cmprojectandroid.viewmodels

import android.util.Log
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

    private val _isFavoritesSelected = mutableStateOf(false)
    val isFavoritesSelected: State<Boolean> = _isFavoritesSelected

    private val _isBusesSelected = MutableStateFlow(true)   // initially buses are selected
    val isBusesSelected: StateFlow<Boolean> = _isBusesSelected

    private val _userLocation = mutableStateOf<LatLng?>(null)
    val userLocation: State<LatLng?> = _userLocation

    fun updateUserLocation(location: LatLng) {
        _userLocation.value = location
    }

    fun setFavoritesFilter(selected: Boolean) {
        _isFavoritesSelected.value = selected
    }

    fun setBusesFilter(selected: Boolean) {
        _isBusesSelected.value = selected
    }

    fun setMapLoaded(loaded: Boolean) {
        _isMapLoaded.value = loaded
    }

    fun updateCameraPosition(position: CameraPosition) {
        viewModelScope.launch {
            _cameraPosition.value = position
        }
    }

    companion object {
        private val DEFAULT_LOCATION = LatLng(40.643771, -8.640994)
        private const val DEFAULT_ZOOM = 13f
    }
}