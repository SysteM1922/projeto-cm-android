package com.example.driverapp.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import com.google.android.gms.location.LocationServices
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.example.driverapp.viewmodels.LocationViewModel
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverPage(
    onLogout: () -> Unit,
    locationViewModel: LocationViewModel = viewModel()
) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val driver_id = auth.currentUser?.uid
    val display_name = auth.currentUser?.displayName
    var hasShift = true
    var shiftCompleted = false
    var trips = emptyList<String>()
    var actualTrip = ""
    var lastStop = ""

    val context = LocalContext.current
    var hasLocationPermission by remember { mutableStateOf(false) }
    var currentLat by remember { mutableStateOf<Double?>(null) }
    var currentLng by remember { mutableStateOf<Double?>(null) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasLocationPermission = isGranted
        if (isGranted) {
            Log.d("DriverPage", "Location permission GRANTED from user action.")
        } else {
            Log.d("DriverPage", "Location permission NOT granted from user action.")
        }
    }

    LaunchedEffect(Unit) {
        val currentStatus = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (currentStatus == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            hasLocationPermission = true
            Log.d("DriverPage", "Location permission is already granted.")
        } else {
            // Permission not granted, request it
            Log.d("DriverPage", "Requesting location permission...")
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // Start location updates if we have the permission
    if (hasLocationPermission) {
        LaunchedEffect(Unit) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            while (true) {
                try {
                    // Attempt to get the last known location
                    val location: Location? = fusedLocationClient.lastLocation.await()

                    location?.let {
                        val lat = it.latitude
                        val lng = it.longitude
                        currentLat = lat
                        currentLng = lng

                        Log.d("DriverPage", "Current location: lat=$lat, lng=$lng")

                        // Update Firestore
                        locationViewModel.updateLocation(lat, lng)
                    }
                } catch (e: SecurityException) {
                    // If for some reason location is revoked in the meantime
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(2000) // Wait 2 seconds before next update
            }
        }
    }

    LaunchedEffect(driver_id) {
        // check if there is any shift assigned to the driver
        Log.d("DriverPage", "display_name: $display_name")
        val shift = firestore.collection("shifts")
            .whereEqualTo("driver_id", driver_id)
            .get()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon Button to logout
        IconButton(
            onClick = {
                auth.signOut()
                onLogout()
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ExitToApp,
                contentDescription = "Logout",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(text = "Driver: $display_name", style = MaterialTheme.typography.headlineMedium)
        // Display latitude and longitude if available
        currentLat?.let { lat ->
            currentLng?.let { lng ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Location: lat=$lat, lng=$lng", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (!hasLocationPermission) {
            Text("You need to accept the location permission for this service.")
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    // Attempt to request again
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            ) {
                Text("Enable Location")
            }

        } else {
            // If permission is granted, show shift information
            when {
                !hasShift -> {
                    Text("No shift assigned")
                }
                shiftCompleted -> {
                    Text("Shift completed")
                }
                else -> {
                    if (actualTrip.isNotEmpty()) {
                        Button(onClick = { /* Navigate to trip details */ }) {
                            Text("Resume trip on $lastStop")
                        }
                    } else {
                        Button(onClick = { /* Navigate to trip details */ }) {
                            Text("Start next trip")
                        }
                    }
                }
            }
        }
    }
}