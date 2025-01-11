package com.example.cmprojectandroid.screens

// THIS IS THE PAGE FOR EACH BUS, COMING FROM THE STOP PAGE
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cmprojectandroid.Model.Bus
import com.example.cmprojectandroid.viewmodels.StopViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopPage(
    stopName: String,
    stopId: String,
    navController: NavHostController,
    viewModel: StopViewModel = viewModel()
) {
    // Observe the list of buses from the ViewModel
    val buses by viewModel.buses.collectAsState()

    var showModal by remember { mutableStateOf(false) }
    var selectedBus by remember { mutableStateOf<Bus?>(null) }

    // fetch the buses from the view model
    LaunchedEffect(stopId) {
        viewModel.fetchBusesForStop(stopId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stopName) }
            )
        }
    ) { paddingValues ->
        if (buses.isEmpty()) {
            // Display a message when no buses are available
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No buses available at this time.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(buses) { bus ->
                    BusItem(
                        bus = bus,
                        onBusDetailsClick = { selectedBus ->
                            navController.navigate("bus_details/${selectedBus.busId}/${Uri.encode(selectedBus.busName)}")
                        },
                        onBellIconClick = { bus ->
                            selectedBus = bus
                            showModal = true
                        }
                    )
                }
            }
            if (showModal && selectedBus != null) {
                NotificationDaysModal(
                    busName = selectedBus!!.busName,
                    busId = selectedBus!!.busId,
                    onDismiss = {
                        // Logic to dismiss the modal
                        showModal = false
                    },
                    onSaveComplete = {
                        // Logic to handle after saving is complete
                        showModal = false
                        // Additional logic if necessary
                    }
                )
            }

        }
    }
}

@Composable
fun BusItem(bus: Bus, onBusDetailsClick: (Bus) -> Unit, onBellIconClick: (Bus) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBusDetailsClick(bus) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bus.busName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Arrival: ${bus.arrivalTime}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row {
                IconButton(
                    onClick = { onBellIconClick(bus) }
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Set Notifications"
                    )
                }
                IconButton(
                    onClick = { onBusDetailsClick(bus) }
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Bus Details"
                    )
                }
            }
        }
    }
}