package com.example.cmprojectandroid.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import androidx.navigation.NavController
import com.example.cmprojectandroid.navigation.NavRoutes

@Composable
fun ProfilePage(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

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
