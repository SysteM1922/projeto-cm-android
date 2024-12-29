package com.example.driverapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.driverapp.Model.Stop

@Composable
fun BottomModal(stop: Stop, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Background overlay could go here if you want
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Card(elevation = CardDefaults.cardElevation(8.dp)) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = stop.name, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}
