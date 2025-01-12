package com.example.cmprojectandroid.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
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
import com.example.cmprojectandroid.viewmodels.MapViewModel
import com.example.cmprojectandroid.viewmodels.PreferencesViewModel


@Composable
fun MapPage(
    navController: NavHostController,
    latitude: Double? = null,
    longitude: Double? = null,
    selectedStopIdInitially: String? = null,
    stopsViewModel: StopsViewModel = viewModel(),
    preferencesViewModel: PreferencesViewModel,
    mapViewModel: MapViewModel
) {

    SideEffect {
        Log.d("MapDebug", """
            MapPage recomposed:
            - MapViewModel hashcode: ${mapViewModel.hashCode()}
            - Latitude: $latitude
            - Longitude: $longitude
            - StopId: $selectedStopIdInitially
            - isMapLoaded: ${mapViewModel.isMapLoaded.value}
        """.trimIndent())
    }

    var lat2 by remember { mutableStateOf(latitude) }
    var lng2 by remember { mutableStateOf(longitude) }

    val context = LocalContext.current
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasLocationPermission = granted
            if (!granted) {
                errorMessage = "Location permission is required to show your current location."
            }
        }
    )
    val isMapLoaded by mapViewModel.isMapLoaded

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

    val isFavoritesSelected by mapViewModel.isFavoritesSelected

    // Compute filtered list of stops
    val filteredStops = remember(stops, favorites, searchQuery, isFavoritesSelected) {
        var temp = stops.filter { stop ->
            stop.stop_name.contains(searchQuery, ignoreCase = true)
        }
        if (isFavoritesSelected) {
            temp = temp.filter { stop ->
                favorites.any { fav -> fav.stop_id == stop.stop_id }
            }
        }
        temp
    }

    // Camera state from ViewModel
    val cameraPositionState = rememberCameraPositionState {
        position = mapViewModel.cameraPosition.value
    }

    // Update ViewModel when camera position changes
    LaunchedEffect(cameraPositionState.position) {
        mapViewModel.updateCameraPosition(cameraPositionState.position)
    }

    // Keyboard controller and focus manager for handling keyboard actions
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current


    // Initialize selected stop based on ViewModel's initial value
    LaunchedEffect(selectedStopIdInitially) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (selectedStopIdInitially?.isNotEmpty() == true) {
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

    // correct bug when the redirected to a stop and it is not a favorite and "Favorites" filter is active
    LaunchedEffect(lat2, lng2, isFavoritesSelected) {
        if (lat2 != null && lng2 != null) {
            // Find the stop corresponding to the given latitude and longitude
            val foundStop = stops.firstOrNull {
                it.stop_lat == lat2 && it.stop_lon == lng2
            }

            // If the stop is not a favorite and "Favorites" filter is active, toggle it off
            if (isFavoritesSelected && (foundStop == null || !favorites.any { it.stop_id == foundStop.stop_id })) {
                mapViewModel.setFavoritesFilter(false)
            }

            // reset lat2 and lng2
            lat2 = null
            lng2 = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            contentPadding = PaddingValues(top = 200.dp, bottom = 100.dp),
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                mapViewModel.setMapLoaded(true)
                // Perform camera update if we have a valid stop
                if (selectedStopIdInitially?.isNotEmpty() == true) {
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
                myLocationButtonEnabled = false,
                compassEnabled = isMapLoaded,
                indoorLevelPickerEnabled = isMapLoaded,
                scrollGesturesEnabled = isMapLoaded,
                zoomGesturesEnabled = isMapLoaded,
                tiltGesturesEnabled = isMapLoaded,
                rotationGesturesEnabled = isMapLoaded,
                scrollGesturesEnabledDuringRotateOrZoom = isMapLoaded,
                mapToolbarEnabled = isMapLoaded
            ),
            onMapClick = { latLng ->
                // Hide keyboard and clear search when map is clicked
                focusManager.clearFocus()
                keyboardController?.hide()
                searchQuery = ""
                if (selectedStop != null) {
                    selectedStop = null
                }
            },
            properties = MapProperties(
                isMyLocationEnabled = hasLocationPermission && isMapLoaded,
            ),
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
                            isFavorite -> BitmapDescriptorFactory.HUE_ORANGE
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
                    },
                )
            }
        }

        if (!mapViewModel.isMapLoaded.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)),
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
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))
                .padding(8.dp) // Inner padding for the column
                .align(Alignment.TopCenter),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
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
                isFavoritesSelected = isFavoritesSelected,
                onFilterChange = { selected ->
                    mapViewModel.setFavoritesFilter(selected)
                }
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
                                // searchQuery = ""
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
                },
                preferencesViewModel = preferencesViewModel
            )
        }

        // 4. Debug Text Overlay (Optional)
        // Column(
        //     modifier = Modifier
        //         .align(Alignment.TopStart)
        //         .padding(8.dp)
        // ) {
        //     // Existing line
        //     Text(
        //         text = "Real-time message: $message",
        //         style = MaterialTheme.typography.bodySmall,
        //         color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        //     )
        //     Text(
        //         text = "VM Hash: ${mapViewModel.hashCode()}",
        //         style = MaterialTheme.typography.bodySmall
        //     )
        //     Text(
        //         text = "Map Loaded: ${mapViewModel.isMapLoaded.value}",
        //         style = MaterialTheme.typography.bodySmall
        //     )
        //     Text(
        //         text = "Lat: $latitude, Lng: $longitude",
        //         style = MaterialTheme.typography.bodySmall
        //     )
        // }
    }
}

@Composable
fun FilterRow(
    isFavoritesSelected: Boolean,
    onFilterChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterRadioButton(
            text = "All",
            selected = !isFavoritesSelected,
            onClick = { onFilterChange(false) }
        )

        Spacer(modifier = Modifier.width(16.dp))

        FilterRadioButton(
            text = "Favorites",
            selected = isFavoritesSelected,
            onClick = { onFilterChange(true) }
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
