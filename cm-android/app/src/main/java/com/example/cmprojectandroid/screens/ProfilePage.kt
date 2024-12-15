package com.example.cmprojectandroid.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.example.cmprojectandroid.navigation.NavRoutes
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cmprojectandroid.viewmodels.TestDataViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProfilePage(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val testDataViewModel: TestDataViewModel = viewModel()
    val coroutineScope = rememberCoroutineScope()

    // Start updating the message every second
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
