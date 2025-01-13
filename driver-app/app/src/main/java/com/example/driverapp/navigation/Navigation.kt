package com.example.driverapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.driverapp.screens.DriverPage
import com.example.driverapp.screens.LoginPage
import com.example.driverapp.screens.NFCReaderPage
import com.example.driverapp.screens.StopPage
import com.example.driverapp.screens.messageScreens.NoConnectionScreen
import com.example.driverapp.screens.messageScreens.SucessScreen
import com.example.driverapp.screens.messageScreens.UnrecognizedCardScreen
import com.example.driverapp.screens.messageScreens.UnrecognizedUserScreen
import com.example.driverapp.viewmodels.DriverViewModel
import com.example.driverapp.viewmodels.NFCViewModel

object NavRoutes {
    const val Login = "login"
    const val DriverPage = "driver_page"
    const val NFCPage = "nfc_page"
    const val StopPage = "stop_page"
    const val NoConnectionScreen = "no_connection_screen"
    const val UnrecognizedCardScreen = "unrecognized_card_screen"
    const val UnrecognizedUserScreen = "unrecognized_user_screen"
    const val SucessScreen = "sucess_screen"
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun NavigationHost(
    navController: NavHostController,
    nfcViewModel: NFCViewModel = viewModel(),    // just one instance
    driverViewModel: DriverViewModel,
    modifier: Modifier = Modifier,
) {

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
                },
                driverViewModel = driverViewModel,
                navController = navController,
            )
        }
        composable(NavRoutes.NFCPage) {
            NFCReaderPage(
                nfcViewModel = nfcViewModel,
                driverViewModel = driverViewModel,
                navController = navController,
            )
        }
        composable(NavRoutes.StopPage) {
            StopPage(
                driverViewModel = driverViewModel,
                navController = navController
            )
        }
        composable(NavRoutes.NoConnectionScreen) {
            NoConnectionScreen(
                navController = navController
            )
        }
        composable(NavRoutes.UnrecognizedCardScreen) {
            UnrecognizedCardScreen(
                navController = navController
            )
        }
        composable(NavRoutes.UnrecognizedUserScreen) {
            UnrecognizedUserScreen(
                navController = navController
            )
        }
        composable(NavRoutes.SucessScreen) {
            SucessScreen(
                navController = navController
            )
        }
    }
}
