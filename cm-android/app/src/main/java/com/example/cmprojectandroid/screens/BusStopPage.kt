package com.example.cmprojectandroid.screens

// THIS IS THE PAGE FOR EACH BUS, COMING FROM THE STOP PAGE
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.cmprojectandroid.Model.Bus
import com.example.cmprojectandroid.viewmodels.PreferencesViewModel
import com.example.cmprojectandroid.viewmodels.StopViewModel


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopPage(
    stopName: String,
    stopId: String,
    navController: NavHostController,
    viewModel: StopViewModel = viewModel(),
    preferencesViewModel: PreferencesViewModel
) {
    // Observe the list of buses from the ViewModel
    val buses by viewModel.buses.collectAsState()

    var showModal by remember { mutableStateOf(false) }
    var selectedBus by remember { mutableStateOf<Bus?>(null) }
    var highlightedBusId by remember { mutableStateOf<String?>(null) }

    // fetch the buses from the view model
    LaunchedEffect(stopId) {
        viewModel.fetchBusesForStop(stopId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stopName) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Navigate back")
                    }
                }
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
                    .fillMaxWidth()
                    .fillMaxHeight(if (showModal) 0.6f else 1f)
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
                            highlightedBusId = bus.busId
                        },
                        isHighlighted = bus.busId == highlightedBusId
                    )
                }
            }
            if (showModal && selectedBus != null) {
                preferencesViewModel.getPreference(selectedBus!!.busId, stopId)?.let {
                    NotificationDaysModal(
                        busName = selectedBus!!.busName,
                        preference = it,
                        onDismiss = {
                            // Logic to dismiss the modal
                            showModal = false
                            highlightedBusId = null
                        },
                        onSaveComplete = { days: List<String>, today: String ->
                            // Logic to handle after saving is complete
                            showModal = false
                            highlightedBusId = null
                            // Additional logic if necessary
                            it.days = days
                            it.today = today
                            preferencesViewModel.updatePreferences(it)
                        }
                    )
                }
            }

        }
    }
}

@Composable
fun BusItem(bus: Bus, onBusDetailsClick: (Bus) -> Unit, onBellIconClick: (Bus) -> Unit, isHighlighted: Boolean) {
    val backgroundColor = if (isHighlighted) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) // Highlighted color
    } else {
        MaterialTheme.colorScheme.surface // Default color
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBusDetailsClick(bus) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
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
                        imageVector = Icons.Default.Notifications,
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