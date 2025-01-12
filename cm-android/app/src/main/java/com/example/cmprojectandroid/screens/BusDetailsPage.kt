package com.example.cmprojectandroid.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.example.cmprojectandroid.Model.Stop
import com.example.cmprojectandroid.viewmodels.BusDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusDetailsPage(
    busId: String,
    busName: String,
    navController: NavHostController,
    viewModel: BusDetailsViewModel = viewModel()
) {
    // Fetch stops for the given busId (mocked)
    LaunchedEffect(busId) {
        viewModel.fetchStopsForBus(busId)
    }

    // Observe the list of stops from the ViewModel
    val stops by viewModel.stops.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Bus: $busName (ID: $busId)") }, // Display the name and ID
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Navigate back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (stops.isEmpty()) {
            // Display a message when no stops are available
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No upcoming stops available.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(stops) { stop ->
                    StopItem(stop = stop, onStopClick = { selectedStop ->
                        navController.navigate(
                            "map?lat=${selectedStop.stop_lat}&lng=${selectedStop.stop_lon}&stopId=${selectedStop.stop_id}"
                        ) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    })
                }
            }
        }
    }
}

@Composable
fun StopItem(stop: Stop, onStopClick: (Stop) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStopClick(stop) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stop.stop_name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Latitude: ${stop.stop_lat}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Longitude: ${stop.stop_lon}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(
                onClick = { onStopClick(stop) }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "View on Map"
                )
            }
        }
    }
}
