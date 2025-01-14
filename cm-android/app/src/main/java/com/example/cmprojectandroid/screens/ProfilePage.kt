package com.example.cmprojectandroid.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cmprojectandroid.Model.Favorite
import com.example.cmprojectandroid.Model.Preference
import com.example.cmprojectandroid.Model.Trip
import com.example.cmprojectandroid.R
import com.example.cmprojectandroid.navigation.NavRoutes
import com.example.cmprojectandroid.viewmodels.PreferencesViewModel
import com.example.cmprojectandroid.viewmodels.TestDataViewModel
import com.example.cmprojectandroid.viewmodels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay


@Composable
fun ExitIcon(onExitClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onExitClick, modifier = modifier) {
        Icon(
            painter = painterResource(id = R.drawable.exit),
            contentDescription = "Exit",
            modifier = Modifier.size(32.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfilePage(
    onLogout: () -> Unit,
    userProfileViewModel: UserProfileViewModel = viewModel(),
    preferencesViewModel: PreferencesViewModel = viewModel()
) {

    var showModal by remember { mutableStateOf(false) }
    var selectedPreference by remember { mutableStateOf<Preference?>(null) }

    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val preferences by preferencesViewModel.preferences.collectAsState()

    val testDataViewModel: TestDataViewModel = viewModel()

    val favorites by userProfileViewModel.favorites.collectAsState()
    val tripHistory by userProfileViewModel.tripHistory.collectAsState()

    var isTripHistoryExpanded by remember { mutableStateOf(false) }
    var isNotificationsExpanded by remember { mutableStateOf(false) }
    var isFavoritesExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { uid ->
            userProfileViewModel.loadUserFavorites(uid)
            userProfileViewModel.fetchTripHistory(uid)
        }
    }

    LaunchedEffect(Unit) {
        var counter = 0
        while (true) {
            val newMessage = "Message number: $counter (time: ${System.currentTimeMillis()})"
            testDataViewModel.updateMessage(newMessage)
            counter++
            delay(1000)
        }
    }


    Box(modifier = Modifier.fillMaxSize()) {  // Wrap everything in a Box
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Profile Page",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Welcome, ${currentUser?.displayName ?: currentUser?.email ?: "User"}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f),
                    fontSize = 24.sp
                )

                ExitIcon(onExitClick = { onLogout() })

            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp) // Add spacing between sections
            ) {
                // Trip History Section
                item {
                    ExpandableSection(
                        title = "Trip History",
                        items = tripHistory,
                        isExpanded = isTripHistoryExpanded,
                        onExpandToggle = { isTripHistoryExpanded = !isTripHistoryExpanded }
                    ) { trip ->
                        TripHistoryCard(trip)
                    }
                }

                item {
                    ExpandableSection(
                        title = "Active Notifications",
                        items = preferences.values.toList(),
                        isExpanded = isNotificationsExpanded,
                        onExpandToggle = { isNotificationsExpanded = !isNotificationsExpanded }
                    ) { preference ->
                        NotificationCard(
                            preference = preference,
                            viewModel = preferencesViewModel,
                            onSettingsClick = {
                                selectedPreference = preference
                                showModal = true
                            }
                        )
                    }
                }

                // Favorite Stops Section
                item {
                    ExpandableSection(
                        title = "Favorite Stops",
                        items = favorites,
                        isExpanded = isFavoritesExpanded,
                        onExpandToggle = { isFavoritesExpanded = !isFavoritesExpanded }
                    ) { favorite ->
                        FavoriteItemCard(
                            favorite = favorite,
                            onRemove = { favoriteToRemove ->
                                userProfileViewModel.removeFavorite(favoriteToRemove)
                            }
                        )
                    }
                }
            }
        }

        if (showModal && selectedPreference != null) {
            NotificationDaysModal(
                busName = selectedPreference!!.trip_short_name,
                preference = selectedPreference!!,
                onDismiss = {
                    showModal = false
                    selectedPreference = null
                },
                onSaveComplete = { days, today ->
                    val updatedPreference = selectedPreference!!.copy(
                        days = days,
                        today = today
                    )
                    preferencesViewModel.updatePreferences(updatedPreference)
                    showModal = false
                    selectedPreference = null
                }
            )
        }
    }
}
@Composable
fun <T> ExpandableSection(
    title: String,
    items: List<T>,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    itemContent: @Composable (T) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandToggle() }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (isExpanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            // Content
            if (isExpanded) {
                if (items.isEmpty()) {
                    Text(
                        text = "No items found",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                } else {
                    items.forEach { item ->
                        itemContent(item)
                    }
                }
            }
        }
    }
}

@Composable
fun TripHistoryCard(trip: Trip) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.bus),
                contentDescription = "Bus Icon",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trip.trip_name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = formatTimestamp(trip.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("HH:mm, MMM dd", java.util.Locale.getDefault())
    return format.format(date)
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotificationCard(preference: Preference, viewModel: PreferencesViewModel, onSettingsClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Trip Name: ${preference.trip_short_name}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Stop Name: ${preference.stop_name}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (preference.days.isNotEmpty()) {
                    Text(
                        text = "Days: ${preference.days.joinToString()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                if (preference.today != "") {
                    Text(
                        text = "Today: ${preference.today}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            // Put a wrench icon at the end of the row
            IconButton(
                onClick = { onSettingsClick() }, // Open the modal
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun FavoriteItemCard(
    favorite: Favorite,
    onRemove: (Favorite) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = favorite.stop_name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Stop ID: ${favorite.stop_id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            IconButton(
                onClick = { onRemove(favorite) },
                modifier = Modifier.size(24.dp) // Adjust size as needed
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove Favorite",
                    tint = MaterialTheme.colorScheme.error // Typically red for delete actions
                )
            }
        }
    }
}