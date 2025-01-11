package com.example.driverapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.driverapp.MainActivity
import com.example.driverapp.screens.DriverPage
import com.example.driverapp.screens.LoginPage
import com.example.driverapp.screens.NFCReaderPage
import com.example.driverapp.screens.ProfilePage
import com.example.driverapp.viewmodels.NFCViewModel

object NavRoutes {
    const val Login = "login"
    const val DriverPage = "driver_page"
    const val NFCPage = "nfc_page"
    const val Profile = "profile"
}

@Composable
fun NavigationHost(navController: NavHostController, nfcViewModel: NFCViewModel, modifier: Modifier = Modifier) {

    NavHost(
        navController,
        startDestination = NavRoutes.Login,
        modifier = modifier
    ) {
        // Authentication routes
        composable(NavRoutes.Login) {
            LoginPage(
                onLoginSuccess = {
                    // Navigate to main app (Map page)
                    navController.navigate(NavRoutes.DriverPage) {
                        // Clear back stack so user can't navigate back to Login
                        popUpTo(NavRoutes.Login) { inclusive = true }
                    }
                }
            )
        }
        // Main app destinations
        composable(NavRoutes.DriverPage) {
            DriverPage(
                onLogout = {
                    navController.navigate(NavRoutes.Login) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable(NavRoutes.NFCPage) {
            NFCReaderPage(nfcViewModel)
        }
        composable(NavRoutes.Profile) {
            ProfilePage(navController)
        }
    }
}
