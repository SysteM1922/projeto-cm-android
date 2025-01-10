package com.example.cmprojectandroid.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cmprojectandroid.Model.Stop
import com.example.cmprojectandroid.viewmodels.StopsViewModel
import com.example.cmprojectandroid.viewmodels.TestDataViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController

@Composable
fun MapPage(
    navController: NavHostController,
    latitude: Double = 40.643771,
    longitude: Double = -8.640994,
    selectedStopIdInitially: String = "",
    stopsViewModel: StopsViewModel = viewModel()
) {

    val testDataViewModel: TestDataViewModel = viewModel()
    val message by testDataViewModel.message.collectAsState()

    // List of stops and favorites
    val stops by stopsViewModel.stops
    val favorites by stopsViewModel.favorites

    // Track the selected stop for modal
    var selectedStop by remember { mutableStateOf<Stop?>(null) }
    val selectedStopId = selectedStop?.stop_id

    // UI states for searching and filtering
    var searchQuery by remember { mutableStateOf("") }
    var filterOption by remember { mutableStateOf("All") }  // "All" or "Favorites"

    // Compute filtered list of stops
    val filteredStops = remember(stops, favorites, searchQuery, filterOption) {
        // 1) Search filter
        var temp = stops.filter { stop ->
            stop.stop_name.contains(searchQuery, ignoreCase = true)
        }
        // 2) Favorites filter
        if (filterOption == "Favorites") {
            temp = temp.filter { stop ->
                favorites.any { fav -> fav.stop_id == stop.stop_id }
            }
        }
        temp
    }

    // Camera state for the map
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 12f)
    }

    // Keyboard controller and focus manager for handling keyboard actions
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var isMapLoading by remember { mutableStateOf(true) }

    LaunchedEffect(selectedStopIdInitially) {
        if (selectedStopIdInitially.isNotEmpty()) {
            val foundStop = stops.firstOrNull { it.stop_id == selectedStopIdInitially }
            if (foundStop != null) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(foundStop.stop_lat, foundStop.stop_lon),
                        15f
                    ),
                    400 // or whatever duration
                )
                selectedStop = foundStop
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                // The map is 100% ready
                isMapLoading = false
                // Perform camera update if we have a valid stop
                if (selectedStopIdInitially.isNotEmpty()) {
                    val foundStop = stops.firstOrNull { it.stop_id == selectedStopIdInitially }
                    if (foundStop != null) {
                        CoroutineScope(Dispatchers.Main).launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(foundStop.stop_lat, foundStop.stop_lon),
                                    15f
                                ),
                                600
                            )
                        }
                        selectedStop = foundStop
                    }
                }
            },
            uiSettings = MapUiSettings(
                zoomControlsEnabled = false,
                myLocationButtonEnabled = true,
                compassEnabled = true,
                indoorLevelPickerEnabled = true,
                scrollGesturesEnabled = true,
                zoomGesturesEnabled = true,
                tiltGesturesEnabled = true,
                rotationGesturesEnabled = true,
                scrollGesturesEnabledDuringRotateOrZoom = true,
                mapToolbarEnabled = true
            ),
            onMapClick = { latLng ->
                // Hide keyboard and clear search when map is clicked
                focusManager.clearFocus()
                keyboardController?.hide()
                searchQuery = ""
            }
            // TODO: ADD the same but when moving the map?
        ) {
            // For each stop in the filtered list
            filteredStops.forEach { stop ->
                val isFavorite = favorites.any { it.stop_id == stop.stop_id }
                val isSelected = (stop.stop_id == selectedStopId)
                Marker(
                    state = MarkerState(
                        position = LatLng(stop.stop_lat, stop.stop_lon)
                    ),
                    title = stop.stop_name,
                    snippet = "Stop ID: ${stop.stop_id}",
                    icon = BitmapDescriptorFactory.defaultMarker(
                        when {
                            isSelected -> BitmapDescriptorFactory.HUE_GREEN
                            else -> BitmapDescriptorFactory.HUE_RED
                        }
                    ),
                    onClick = { marker ->
                        selectedStop = stop
                        // Animate camera to the selected stop smoothly
                        CoroutineScope(Dispatchers.Main).launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(stop.stop_lat, stop.stop_lon),
                                    15f // Adjust zoom as needed
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

        if (isMapLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        // Slightly dim the background
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // 2. Overlay for Search Bar and Filters at the Top
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // Adjust padding as needed
                .background(Color.White.copy(alpha = 0.8f)) // Semi-transparent background
                .padding(8.dp) // Inner padding for the column
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search stops by name") },
                singleLine = true,
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // b. Filter Row (All | Favorites)
            FilterRow(
                filterOption = filterOption,
                onFilterChange = { filterOption = it }
            )

            // c. Search Results Dropdown
            if (searchQuery.isNotEmpty() && filteredStops.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp) // Limit the height
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        items(filteredStops) { stop ->
                            SearchResultItem(stop = stop, onItemClick = {
                                selectedStop = it
                                // Animate camera to the selected stop smoothly
                                CoroutineScope(Dispatchers.Main).launch {
                                    cameraPositionState.animate(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(it.stop_lat, it.stop_lon),
                                            15f // Adjust zoom as needed
                                        ),
                                        400 // Duration of camera animation in ms
                                    )
                                }
                                // Clear search and hide keyboard
                                searchQuery = ""
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            })
                        }
                    }
                }
            }
        }

        // 3. Bottom Modal if a Stop is Selected
        selectedStop?.let { stop ->
            BottomModal(
                stop = stop,
                isFavorite = favorites.any { it.stop_id == stop.stop_id },
                onDismiss = { selectedStop = null },
                navController = navController,
                onFavoriteClick = {
                    stopsViewModel.toggleFavorite(it)
                }
            )
        }

        // 4. Debug Text Overlay (Optional)
        Text(
            text = "Real-time message: $message",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        )
    }
}

@Composable
fun FilterRow(
    filterOption: String,
    onFilterChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "All" radio button
        FilterRadioButton(
            text = "All",
            selected = (filterOption == "All"),
            onClick = { onFilterChange("All") }
        )

        Spacer(modifier = Modifier.width(16.dp))

        // "Favorites" radio button
        FilterRadioButton(
            text = "Favorites",
            selected = (filterOption == "Favorites"),
            onClick = { onFilterChange("Favorites") }
        )
    }
}

/**
 * Helper composable for individual radio buttons.
 */
@Composable
fun FilterRadioButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}


@Composable
fun SearchResultItem(
    stop: Stop,
    onItemClick: (Stop) -> Unit
) {
    // Use a Card to give a slight elevation and rounded corners to each item
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(stop) }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        // Row to layout the stop name and a forward arrow icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stop Name
            Text(
                text = stop.stop_name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )

            // Forward Arrow Icon to indicate navigable action
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate to ${stop.stop_name}",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
