package com.example.cmprojectandroid.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.cmprojectandroid.screens.*
import com.example.cmprojectandroid.viewmodels.MainViewModel
import com.example.cmprojectandroid.viewmodels.MapViewModel


object NavRoutes {
    const val Login = "login"
    const val SignUp = "sign_up"
    const val StopPage = "stop_page/{stopName}"
    const val MapPage = "map?lat={lat}&lng={lng}&stopId={stopId}"
}

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {

    object Map : BottomNavItem(
        route = "map?lat={lat}&lng={lng}&stopId={stopId}",
        title = "Map",
        icon = Icons.Default.Star
    )
    object ScanQRCode : BottomNavItem("scan_qrcode", "Scan QR Code", Icons.Default.Star)
    object NFCPage : BottomNavItem("nfc_page", "NFC Page", Icons.Default.Star)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}

@Composable
fun NavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val mainViewModel = remember { MainViewModel() }
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
        composable(
            route = "map?lat={lat}&lng={lng}&stopId={stopId}",
            arguments = listOf(
                navArgument("lat") {
                    type = NavType.StringType
                    defaultValue = "40.643771"
                },
                navArgument("lng") {
                    type = NavType.StringType
                    defaultValue = "-8.640994"
                },
                navArgument("stopId") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->

            // Now retrieving them from the arguments is fine
            val latString = backStackEntry.arguments?.getString("lat") ?: "40.643771"
            val lngString = backStackEntry.arguments?.getString("lng") ?: "-8.640994"
            val stopId    = backStackEntry.arguments?.getString("stopId") ?: ""

            MapPage(
                navController = navController,
                latitude = latString.toDoubleOrNull(),
                longitude = lngString.toDoubleOrNull(),
                selectedStopIdInitially = stopId,
                mapViewModel = mainViewModel.mapViewModel
            )
        }

        composable(BottomNavItem.ScanQRCode.route) {
            ScanQRCodePage(navController = navController)
        }

        composable("hello_page") {
            HelloWorldPage()
        }

        composable(BottomNavItem.NFCPage.route) {
            NFCPage(LocalContext.current)
        }

        composable(BottomNavItem.Profile.route) {
            ProfilePage(navController)
        }

        // StopPage route with stopName as StringType
        composable(
            route = NavRoutes.StopPage,
            arguments = listOf(navArgument("stopName") { type = NavType.StringType })
        ) { backStackEntry ->
            val stopName = backStackEntry.arguments?.getString("stopName")?.let { Uri.decode(it) } ?: "Unknown Stop"
            StopPage(
                stopName = stopName,
                onBusDetailsClick = { bus ->
                    // Navigate to Bus Details Page with busId
                    navController.navigate("bus_details/${bus.busId}")
                }
            )
        }

        // BusDetailsPage route with navController passed
        composable(
            route = "bus_details/{busId}",
            arguments = listOf(navArgument("busId") { type = NavType.StringType })
        ) { backStackEntry ->
            val busId = backStackEntry.arguments?.getString("busId") ?: "Unknown Bus"
            BusDetailsPage(busId = busId, navController = navController)
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
                selected = currentRoute?.startsWith(item.route) == true,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
