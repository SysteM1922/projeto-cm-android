package com.example.cmprojectandroid.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.cmprojectandroid.screens.HelloWorldPage
import com.example.cmprojectandroid.screens.LoginPage
import com.example.cmprojectandroid.screens.SignUpPage
import com.example.cmprojectandroid.screens.MapPage
import com.example.cmprojectandroid.screens.NFCPage
import com.example.cmprojectandroid.screens.ProfilePage
import com.example.cmprojectandroid.screens.ScanQRCodePage

object NavRoutes {
    const val Login = "login"
    const val SignUp = "sign_up"
}


sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    data object Map : BottomNavItem("map", "Map", Icons.Default.Star)
    data object ScanQRCode : BottomNavItem("scan_qrcode", "Scan QR Code", Icons.Default.Star)
    data object NFCPage : BottomNavItem("nfc_page", "NFC Page", Icons.Default.Star)
    data object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}

@Composable
fun NavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController,
        startDestination = NavRoutes.Login,
        modifier = modifier
    ) {
        // Authentication routes
        composable(NavRoutes.Login) {
            LoginPage(
                onNavigateToSignUp = {
                    navController.navigate(NavRoutes.SignUp)
                },
                onLoginSuccess = {
                    // Navigate to main app (Map page)
                    navController.navigate(BottomNavItem.Map.route) {
                        // Clear back stack so user can't navigate back to Login
                        popUpTo(NavRoutes.Login) { inclusive = true }
                    }
                }
            )
        }
        composable(NavRoutes.SignUp) {
            SignUpPage(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    // Navigate to main app (Map page)
                    navController.navigate(BottomNavItem.Map.route) {
                        popUpTo(NavRoutes.Login) { inclusive = true }
                    }
                }
            )
        }
        // Main app destinations
        composable(BottomNavItem.Map.route) {
            MapPage()
        }
        composable(BottomNavItem.ScanQRCode.route) {
            ScanQRCodePage(navController = navController)
        }

        composable("hello_page") {
            HelloWorldPage()
        }

        composable(BottomNavItem.NFCPage.route) {
            NFCPage()
        }
        composable(BottomNavItem.Profile.route) {
            ProfilePage(navController)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Map,
        BottomNavItem.ScanQRCode,
        BottomNavItem.NFCPage,
        BottomNavItem.Profile
    )
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination to avoid building up a large back stack
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}
