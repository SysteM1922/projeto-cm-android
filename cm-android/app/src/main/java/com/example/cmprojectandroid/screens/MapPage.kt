package com.example.cmprojectandroid.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cmprojectandroid.Model.Stop
import com.example.cmprojectandroid.viewmodels.StopsViewModel
import com.example.cmprojectandroid.viewmodels.TestDataViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MapPage(stopsViewModel: StopsViewModel = viewModel()) {

    val testDataViewModel: TestDataViewModel = viewModel()
    val message by testDataViewModel.message.collectAsState()

    val stops by stopsViewModel.stops
    var selectedStop by remember { mutableStateOf<Stop?>(null) }

    // We'll also keep a selectedStopId for easy comparison
    val selectedStopId = selectedStop?.id

    // Camera position state
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(40.643771, -8.640994), 12f)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            stops.forEach { stop ->
                Marker(
                    state = MarkerState(
                        position = LatLng(stop.latitude, stop.longitude)
                    ),
                    title = stop.name,
                    snippet = "Stop ID: ${stop.id}",
                    icon = BitmapDescriptorFactory.defaultMarker(
                        if (stop.id == selectedStopId)
                            BitmapDescriptorFactory.HUE_GREEN
                        else
                            BitmapDescriptorFactory.HUE_RED
                    ),
                    onClick = { marker ->
                        // Update selected stop
                        selectedStop = stop

                        // Animate camera to the selected stop smoothly
                        CoroutineScope(Dispatchers.Main).launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(stop.latitude, stop.longitude),
                                    13f // Adjust zoom as needed
                                ),
                                400 // Duration of camera animation in ms
                            )
                        }
                        // Return true to consume the click event and prevent default info window
                        true
                    }
                )
            }
        }

        // Debug text
        Text(
            text = "Real-time message: $message",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )

        // Bottom modal if a stop is selected
        selectedStop?.let { stop ->
            BottomModal(stop = stop) {
                selectedStop = null // Dismiss modal, no stop selected -> markers back to red
            }
        }
    }
}
