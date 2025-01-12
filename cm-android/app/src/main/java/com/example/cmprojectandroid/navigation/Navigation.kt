package com.example.cmprojectandroid.navigation

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.cmprojectandroid.R
import com.example.cmprojectandroid.screens.*
import com.example.cmprojectandroid.viewmodels.MainViewModel
import com.example.cmprojectandroid.viewmodels.MapViewModel
import com.example.cmprojectandroid.viewmodels.PreferencesViewModel


object NavRoutes {
    const val Login = "login"
    const val SignUp = "sign_up"
    const val StopPage = "stop_page/{stopName}/{stopId}"
    const val MapPage = "map?lat={lat}&lng={lng}&stopId={stopId}"
}

sealed class BottomNavItem(val route: String, val title: String) {

    object Map : BottomNavItem(
        route = "map?lat={lat}&lng={lng}&stopId={stopId}",
        title = "Map"
    )

    object ScanQRCode : BottomNavItem("scan_qrcode", "Scan QR Code")
    object NFCPage : BottomNavItem("nfc_page", "NFC Page")
    object Profile : BottomNavItem("profile", "Profile")
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
    val mainViewModel = remember { MainViewModel() }
    val preferencesViewModel = viewModel<PreferencesViewModel>()
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
                    preferencesViewModel.subscribeAllTopics()
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
            val stopId = backStackEntry.arguments?.getString("stopId") ?: ""

            MapPage(
                navController = navController,
                latitude = latString.toDoubleOrNull(),
                longitude = lngString.toDoubleOrNull(),
                selectedStopIdInitially = stopId,
                mapViewModel = mainViewModel.mapViewModel,
                preferencesViewModel = preferencesViewModel
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
            ProfilePage(onLogout = {
                preferencesViewModel.unsubscribeAllTopics()
                navController.navigate(NavRoutes.Login) {
                    popUpTo(0)
                }
            })
        }

        composable(
            route = NavRoutes.StopPage,
            arguments = listOf(
                navArgument("stopName") { type = NavType.StringType },
                navArgument("stopId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val stopName = backStackEntry.arguments?.getString("stopName")?.let { Uri.decode(it) }
                ?: "Unknown Stop"
            val stopId = backStackEntry.arguments?.getString("stopId") ?: "Unknown Stop ID"
            StopPage(
                stopName = stopName,
                stopId = stopId,
                navController = navController,
                preferencesViewModel = preferencesViewModel
            )
        }

        // BusDetailsPage route with navController passed
        composable(
            route = "bus_details/{busId}/{busName}",
            arguments = listOf(
                navArgument("busId") { type = NavType.StringType },
                navArgument("busName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val busId = backStackEntry.arguments?.getString("busId") ?: "Unknown Bus ID"
            val busName = backStackEntry.arguments?.getString("busName")?.let { Uri.decode(it) }
                ?: "Unknown Bus Name"

            BusDetailsPage(
                busId = busId,
                busName = busName,
                navController = navController
            )
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
            val icon: Painter = when (item) {
                BottomNavItem.Map -> painterResource(id = R.drawable.map)
                BottomNavItem.ScanQRCode -> painterResource(id = R.drawable.qrcode_24)
                BottomNavItem.NFCPage -> painterResource(id = R.drawable.nfc)
                BottomNavItem.Profile -> painterResource(id = R.drawable.profile)
            }
            NavigationBarItem(
                icon = { Icon(painter = icon, contentDescription = item.title) },
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
