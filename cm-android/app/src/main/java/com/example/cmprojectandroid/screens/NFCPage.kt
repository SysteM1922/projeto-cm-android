package com.example.cmprojectandroid.screens

import android.app.Activity.MODE_PRIVATE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.cmprojectandroid.R

import com.example.cmprojectandroid.services.HCEService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NFCPage(context: Context) {

    val packageManager = context.packageManager
    val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
    val lifecycleScope = rememberCoroutineScope()
    var isNfcEnabled by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }
    val navController = rememberNavController()

    fun checkNfcStatus() {
        isNfcEnabled = nfcAdapter?.isEnabled ?: false
    }

    LaunchedEffect(Unit) {
        packageManager.setComponentEnabledSetting(
            ComponentName(context, HCEService::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        Log.d("HCEService", "Starting HCEService")

        lifecycleScope.launch {
            if (nfcAdapter != null) {
                while (true) {
                    checkNfcStatus()
                    if (!isNfcEnabled) {
                        showModal = true
                    }
                    delay(500)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            packageManager.setComponentEnabledSetting(
                ComponentName(context, HCEService::class.java),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
            Log.d("HCEService", "Stopping HCEService")
        }
    }

    Box {
        if (showModal) {
            AlertDialog(
                onDismissRequest = { showModal = false },
                title = { Text("NFC Error") },
                text = { Text("NFC is not enabled on this device. Please enable it.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showModal = false
                            //context.startActivity(android.content.Intent(android.provider.Settings.ACTION_NFC_SETTINGS))
                        }
                    ) {
                        Text("Ok")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showModal = false
                            navController.navigate("map")
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Please tap your card on the NFC reader.",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                textAlign = TextAlign.Center
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.card),
                    contentDescription = "NFC Card",
                    modifier = Modifier
                        .shadow(
                            elevation = 8.dp,
                            shape = MaterialTheme.shapes.extraLarge,
                        ),
                )
            }
        }
    }
}