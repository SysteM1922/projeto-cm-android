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
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.driverapp.navigation.NavRoutes
import com.example.driverapp.services.LocationService
import com.example.driverapp.viewmodels.DriverViewModel


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun DriverPage(
    onLogout: () -> Unit,
    driverViewModel: DriverViewModel,
    navController: NavHostController,
) {
    val auth = FirebaseAuth.getInstance()
    val displayName = auth.currentUser?.displayName

    val context = LocalContext.current
    var hasBackgroundServicePermission by remember { mutableStateOf(false) }

    var tripId by remember { mutableStateOf("") }
    var stopSequence by remember { mutableStateOf(0) }

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

        driverViewModel.fetchDriverData(auth.currentUser?.uid)

        driverViewModel.currentPage = if (driverViewModel.isTripActive()) 1 else 0
    }

    val stopTimes by remember { derivedStateOf { driverViewModel.stopTimes } }

    // Handle navigation based on currentPage -- maybe this is a better approach
//    LaunchedEffect(driverViewModel.currentPage) {
//        when (driverViewModel.currentPage) {
//            1 -> {
//                stopSequence++
//                Log.d("DriverPage", "Stop Sequence: $stopSequence")
//                Log.d("DriverPage", "Stop Times size: ${stopTimes.size}")
//                if (stopSequence >= stopTimes.size) {
//                    stopSequence = 0
//                    val serviceIntent = Intent(context, LocationService::class.java).apply {
//                        action = LocationService.ACTION_STOP
//                    }
//                    context.startService(serviceIntent)
//                    driverViewModel.endTrip()
//                } else {
//                    Log.d("DriverPage", "Navigating to StopPage")
//                    navController.navigate(NavRoutes.StopPage)
//                }
//            }
//            2 -> {
//                Log.d("DriverPage", "Navigating to NFCPage")
//                navController.navigate(NavRoutes.NFCPage)
//            }
//        }
//    }

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
        Text(text = "Driver: $displayName", style = MaterialTheme.typography.headlineMedium)

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
            if (driverViewModel.currentPage == 0) {
                // Input trip ID
                Spacer(modifier = Modifier.weight(0.5f))
                Text(
                    text = "Bus",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = tripId,
                    onValueChange = { tripId = it },
                    label = { Text("Enter Trip ID") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        driverViewModel.startTrip(tripId)
                        val serviceIntent = Intent(context, LocationService::class.java).apply {
                            action = LocationService.ACTION_START
                        }
                        context.startService(serviceIntent)
                    },
                ) {
                    Text("Start Trip")
                }
                Spacer(modifier = Modifier.weight(1f))

            // SE DERES UNCOMMENT AQUELE LAUNCHED EFFECT LÁ EM CIMA, NÃO PRECISAS DESTES 2 "ELSE IF" acho eu
            } else if (driverViewModel.currentPage == 1) {
                // Display Stop Button
                Log.d("DriverPage", "StopPage")
                // go to stop page automatically
                LaunchedEffect(Unit) {
                    // this runs only once
                    stopSequence++
                    Log.d("DriverPage", "Stop Sequence: $stopSequence")
                    Log.d("DriverPage", "Stop Times size: ${stopTimes.size}")
                    if (stopSequence >= stopTimes.size) {
                        stopSequence = 0
                        val serviceIntent = Intent(context, LocationService::class.java).apply {
                            action = LocationService.ACTION_STOP
                        }
                        context.startService(serviceIntent)
                        driverViewModel.endTrip()
                    } else {
                        Log.d("DriverPage", "Navigating to StopPage")
                        navController.navigate(NavRoutes.StopPage)
                    }
                }
            } else if (driverViewModel.currentPage == 2) {
                Log.d("DriverPage", "NFCPage")
                // Open NFC Page
                navController.navigate(NavRoutes.NFCPage)
            }
        }
    }
}
