package com.example.cmprojectandroid.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    navController: NavController,
    userProfileViewModel: UserProfileViewModel = viewModel()
) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val testDataViewModel: TestDataViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()

    val favorites by userProfileViewModel.favorites.collectAsState()

    // When this composable is first displayed, load the user's favorites
    LaunchedEffect(currentUser) {
        currentUser?.uid?.let { uid ->
            userProfileViewModel.loadUserFavorites(uid)
        }
    }

    // Start updating the message every second (for testing/debugging)
    LaunchedEffect(Unit) {
        var counter = 0
        while (true) {
            val newMessage = "Message number: $counter (time: ${System.currentTimeMillis()})"
            testDataViewModel.updateMessage(newMessage)
            counter++
            delay(1000) // wait 1 second before updating again
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

        Text(text = "Welcome, ${currentUser?.displayName ?: currentUser?.email ?: "User"}")

        Spacer(modifier = Modifier.height(16.dp))

        // Favorites section
        Text(
            text = "Your Favorites",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (favorites.isEmpty()) {
            // If no favorites, show a simple text
            Text(text = "No favorites found.")
        } else {
            // Display favorites in a LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Fill available vertical space
            ) {
                items(favorites) { favoriteItem ->
                    FavoriteItemCard(
                        favorite = favoriteItem,
                        onRemove = { favoriteToRemove ->
                            userProfileViewModel.removeFavorite(favoriteToRemove)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                auth.signOut()
                navController.navigate(NavRoutes.Login) {
                    popUpTo(0)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}


@Composable
fun FavoriteItemCard(
    favorite: Favorite,
    onRemove: (Favorite) -> Unit // Callback to handle removal
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
                    text = favorite.name,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Stop ID: ${favorite.id}",
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