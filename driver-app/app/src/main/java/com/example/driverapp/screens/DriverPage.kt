package com.example.driverapp.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.driverapp.repositories.LocationRepo
import com.example.driverapp.services.LocationService


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun DriverPage(
    onLogout: () -> Unit,
) {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val driver_id = auth.currentUser?.uid
    val display_name = auth.currentUser?.displayName
    var trips = emptyList<String>()
    var actualTrip = ""
    var lastStop = ""

    val context = LocalContext.current
    var hasBackgroundServicePermission by remember { mutableStateOf(false) }
    var currentLat by remember { mutableStateOf<Double?>(null) }
    var currentLng by remember { mutableStateOf<Double?>(null) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            hasBackgroundServicePermission = true
            Log.d("DriverPage", "Foreground service permission granted.")
        } else {
            hasBackgroundServicePermission = false
            Log.d("DriverPage", "Foreground service permission denied.")
        }
    }

    LaunchedEffect(Unit) {
        val currentForegroundServiceStatus = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        )
        if (currentForegroundServiceStatus == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted
            hasBackgroundServicePermission = true
            Log.d("DriverPage", "Foreground service permission is already granted.")
        } else {
            // Permission not granted, request it
            Log.d("DriverPage", "Requesting foreground service permission...")
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
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
                Text(
                    text = "Location: lat=$lat, lng=$lng",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (!hasBackgroundServicePermission) {
            Text("You need to accept the foreground service permission for this service.")
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    // Attempt to request again
                    Log.d("DriverPage", "Requesting foreground service permission...")
                    // open app location settings
                    val intent = Intent()
                    intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    intent.data = android.net.Uri.parse("package:" + context.packageName)
                    context.startActivity(intent)
                }
            ) {
                Text("Enable Background Service")
            }
        } else {
            Button(onClick = {
                // Start the location service
                val serviceIntent = Intent(context, LocationService::class.java).apply {
                    action = LocationService.ACTION_START
                }
                context.startService(serviceIntent)
            }) {
                Text("Start next trip")
            }
            Button(onClick = {
                // Stop the location service
                val serviceIntent = Intent(context, LocationService::class.java).apply {
                    action = LocationService.ACTION_STOP
                }
                context.startService(serviceIntent)
            }) {
                Text("Stop trip")
            }
        }
    }
}