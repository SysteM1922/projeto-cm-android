package com.example.cmprojectandroid

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cmprojectandroid.navigation.BottomNavItem
import com.example.cmprojectandroid.navigation.BottomNavigationBar
import com.example.cmprojectandroid.navigation.NavRoutes
import com.example.cmprojectandroid.navigation.NavigationHost
import com.example.cmprojectandroid.ui.theme.CmProjectAndroidTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            CmProjectAndroidTheme {
                MainScreen()
            }
        }
        requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
    }
}


@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            // User is logged in, navigate to Map page
            navController.navigate(BottomNavItem.Map.route) {
                popUpTo(0)
            }
        } else {
            // User is not logged in, navigate to Login page
            navController.navigate(NavRoutes.Login) {
                popUpTo(0)
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = when {
        currentRoute?.startsWith(NavRoutes.Login) == true -> false
        currentRoute?.startsWith(NavRoutes.SignUp) == true -> false
        else -> true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavigationHost(navController, Modifier.padding(innerPadding))
    }
}


