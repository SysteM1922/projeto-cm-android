package com.example.driverapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun DriverPage() {
    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val driver_id = auth.currentUser?.uid
    val display_name = auth.currentUser?.displayName
    var hasShift = true
    var shiftCompleted = false
    var trips = emptyList<String>()
    var actualTrip = ""
    var lastStop = ""

    LaunchedEffect(driver_id) {
        // check if there is any shift assigned to the driver
        val shift = firestore.collection("shifts")
            .whereEqualTo("driver_id", driver_id)
            .get()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Driver: $display_name", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        if (!hasShift) {
            Text("No shift assigned")
        } else if (shiftCompleted) {
            Text("Shift completed")
        } else {
            if (actualTrip.isNotEmpty()) {
                Button(
                    onClick = { /* Navigate to the trip details screen */ }
                ) {
                    Text("Resume trip on $lastStop")
                }
            } else {
                Button(
                    onClick = { /* Navigate to the trip details screen */ }
                ) {
                    Text("Start next trip")
                }
            }
        }
    }
}