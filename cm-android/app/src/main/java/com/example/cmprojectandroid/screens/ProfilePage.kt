package com.example.cmprojectandroid.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.cmprojectandroid.Model.Favorite
import com.example.cmprojectandroid.navigation.NavRoutes
import com.example.cmprojectandroid.viewmodels.TestDataViewModel
import com.example.cmprojectandroid.viewmodels.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay


@Composable
fun ProfilePage(
    onLogout: () -> Unit,
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val testDataViewModel: TestDataViewModel = viewModel()

    val favorites by userProfileViewModel.favorites.collectAsState()

    // Mocked data for trip history and active notifications
    val tripHistory = remember {
        mutableStateListOf(
            "Bus A (10:00 AM, Jan 10)",
            "Bus B (2:30 PM, Jan 9)",
            "Bus C (8:15 AM, Jan 8)"
        )
    }
    val activeNotifications = remember {
        mutableStateListOf(
            "Bus A, Stop 1 (Mon, Wed, Fri)",
            "Bus B, Stop 3 (Tue, Thu)",
            "Bus C, Stop 5 (Weekends)"
        )
    }

    var isTripHistoryExpanded by remember { mutableStateOf(false) }
    var isNotificationsExpanded by remember { mutableStateOf(false) }
    var isFavoritesExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { uid ->
            userProfileViewModel.loadUserFavorites(uid)
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
                modifier = Modifier.weight(1f) // Makes the text take up available space
            )

            Button(
                onClick = {
                    auth.signOut()
                    onLogout()
                }
            ) {
                Text("Logout")
            }
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
                    items = activeNotifications,
                    isExpanded = isNotificationsExpanded,
                    onExpandToggle = { isNotificationsExpanded = !isNotificationsExpanded }
                ) { notification ->
                    NotificationCard(notification)
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
fun TripHistoryCard(trip: String) {
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
                imageVector = Icons.Default.ThumbUp,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                val (busInfo, dateTime) = trip.split(" (", ")")
                Text(
                    text = busInfo,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dateTime,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun NotificationCard(notification: String) {
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
                    .size(24.dp)
                    .padding(end = 12.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                val parts = notification.split(", ")
                Text(
                    text = "${parts[0]}, ${parts[1].split(" (")[0]}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = parts[1].split(" (")[1].removeSuffix(")"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Put a wrench icon at the end of the row
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "Settings",
                tint = MaterialTheme.colorScheme.primary
            )
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