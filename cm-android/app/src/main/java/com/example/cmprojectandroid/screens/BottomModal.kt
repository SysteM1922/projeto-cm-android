package com.example.cmprojectandroid.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.cmprojectandroid.Model.Stop
import com.example.cmprojectandroid.Model.Driver
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.cmprojectandroid.Model.StopOrDriver
import com.example.cmprojectandroid.viewmodels.PreferencesViewModel


@Composable
fun BottomModal(
    data: StopOrDriver,
    isFavorite: Boolean, // Receive favorite status as a parameter
    onDismiss: () -> Unit,
    navController: NavHostController,
    onFavoriteClick: (StopOrDriver.Stop) -> Unit = {},
    preferencesViewModel: PreferencesViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Semi-transparent scrim
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header with title and favorite button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val name = when (data) {
                        is StopOrDriver.Stop -> data.stopName
                        is StopOrDriver.Driver -> data.driverName
                    }
                    Text(
                        text = name,
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    if (data is StopOrDriver.Stop) { // Check if it's a Stop
                        IconButton(
                            onClick = { onFavoriteClick(data) }, // Now safe to cast
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                                tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Close")
                    }

                    Button(
                        // navController.navigate("stop_page/${Uri.encode(stop.stop_name)}/${stop.stop_id}"
                        onClick = {
                            val route = when (data) {
                                is StopOrDriver.Stop -> "stop_page/${Uri.encode(data.stopName)}/${data.stopId}"
                                is StopOrDriver.Driver -> "bus_details/${Uri.encode(data.driverId)}/${data.driverName}"
                            }
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Details")
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = "View details",
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
