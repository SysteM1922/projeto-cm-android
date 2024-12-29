package com.example.driverapp.screens

import android.content.Intent
import android.nfc.NfcAdapter
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.example.driverapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.driverapp.NFC_READER_CLOSED
import com.example.driverapp.NFC_READER_OPENED

@Composable
fun NFCReaderPage() {

    val context = LocalContext.current
    val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
    val lifecycleScope = rememberCoroutineScope()
    var isNfcEnabled by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }
    val navController = rememberNavController()

    fun checkNfcStatus() {
        isNfcEnabled = nfcAdapter?.isEnabled ?: false
    }

    LaunchedEffect(Unit) {
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

        context.sendBroadcast(Intent(NFC_READER_OPENED))
    }

    DisposableEffect(Unit) {
        onDispose {
            context.sendBroadcast(Intent(NFC_READER_CLOSED))
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
                            navController.navigate("hello_world")
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        Column(
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        )
        {
            Text(
                "Waiting for NFC card...",
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            Icon(
                painter = painterResource(id = R.drawable.contactless),
                contentDescription = "NFC Icon",
            )
            Text(
                "Please tap your card on the back of this device to validate.",
                fontSize = 24.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NFCReaderPagePreview() {
    NFCReaderPage()
}