package com.example.cmprojectandroid.screens

import android.app.Activity.MODE_PRIVATE
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cmprojectandroid.R

import com.example.cmprojectandroid.services.HCEService

@Composable
fun NFCPage(context: Context) {

    val packageManager = context.packageManager

    LaunchedEffect(Unit) {
        packageManager.setComponentEnabledSetting(
            ComponentName(context, HCEService::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        Log.d("HCEService", "Starting HCEService")
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