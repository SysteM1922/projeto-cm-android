package com.example.driverapp.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.driverapp.MainActivity
import com.example.driverapp.screens.HelloWorldPage
import com.example.driverapp.screens.LoginPage
import com.example.driverapp.screens.SignUpPage
import com.example.driverapp.screens.NFCReaderPage
import com.example.driverapp.screens.ProfilePage
import com.example.driverapp.viewmodels.NFCViewModel

object NavRoutes {
    const val Login = "login"
    const val SignUp = "sign_up"
}


sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    data object Map : BottomNavItem("hello_world", "Hello World", Icons.Default.Star)
    data object ScanQRCode : BottomNavItem("hello_world", "Hello World", Icons.Default.Star)
    data object NFCPage : BottomNavItem("nfc_page", "NFC Page", Icons.Default.Star)
    data object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}

@Composable
fun NavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {

    val context = LocalContext.current

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
            HelloWorldPage()
        }
        composable(BottomNavItem.ScanQRCode.route) {
            HelloWorldPage()
        }

        composable(BottomNavItem.NFCPage.route) {
            NFCReaderPage(NFCViewModel())
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
