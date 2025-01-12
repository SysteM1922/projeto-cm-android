package com.example.cmprojectandroid

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cmprojectandroid.navigation.BottomNavItem
import com.example.cmprojectandroid.navigation.BottomNavigationBar
import com.example.cmprojectandroid.navigation.NavRoutes
import com.example.cmprojectandroid.navigation.NavigationHost
import com.example.cmprojectandroid.services.PushNotificationManager
import com.example.cmprojectandroid.ui.theme.CmProjectAndroidTheme
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class MainActivity : ComponentActivity() {

    private fun retrieveFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("push notification device token", "failed with error: ${task.exception}")
                return@OnCompleteListener
            }
            val token = task.result
            Log.d("push notification device token", "token received: $token")
            lifecycleScope.launch {
                PushNotificationManager.registerTokenOnServer(token)
            }
        })
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (!isGranted) {
            AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("Please allow the app to post notifications")
                .setPositiveButton("OK") { dialog, _ ->
                    // open app notification settings
                    val intent = Intent()
                    intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                    intent.putExtra("android.provider.extra.APP_PACKAGE", packageName)
                    startActivity(intent)
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                    exitProcess(0)
                }
                .show()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                return
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

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

        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        askNotificationPermission()
        retrieveFCMToken()
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


