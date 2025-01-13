package com.example.driverapp.screens

import android.content.Context
import android.net.ConnectivityManager
import android.nfc.NfcAdapter
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.driverapp.R
import com.example.driverapp.navigation.NavRoutes
import com.example.driverapp.screens.messageScreens.NoConnectionScreen
import com.example.driverapp.screens.messageScreens.SucessScreen
import com.example.driverapp.screens.messageScreens.UnrecognizedCardScreen
import com.example.driverapp.screens.messageScreens.UnrecognizedUserScreen
import com.example.driverapp.viewmodels.DriverViewModel
import com.example.driverapp.viewmodels.NFCViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun NFCReaderPage(
    nfcViewModel: NFCViewModel,
    driverViewModel: DriverViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    val nfcAdapter = NfcAdapter.getDefaultAdapter(context)
    val lifecycleScope = rememberCoroutineScope()
    var isNfcEnabled by remember { mutableStateOf(false) }
    var showModal by remember { mutableStateOf(false) }
    val cardID = nfcViewModel.cardID.value // CHANGED this -- we can just access it
    var oldCardID = ""
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    var showScreen by remember { mutableStateOf(false) }

    fun checkNfcStatus() {
        isNfcEnabled = nfcAdapter?.isEnabled ?: false
    }

    @Composable
    fun handleCardReaded(cardID: String) {

        val activeNetwork = connectivityManager.activeNetworkInfo
        val isConnected = activeNetwork?.isConnectedOrConnecting == true

        if (!isConnected) {
            NoConnectionScreen()
        }

        if (cardID.isEmpty()) {
            UnrecognizedCardScreen()
        } else {
            val user = driverViewModel.validateCard(cardID)
            if (user.isNotEmpty()) {
                SucessScreen(user)
            } else {
                UnrecognizedUserScreen()
            }
        }
    }

    LaunchedEffect(Unit) {

        Log.d("ReadNFCActivity", "NFC Page Visible")
        nfcViewModel.isNFCPageVisible = true

        lifecycleScope.launch {
            if (nfcAdapter != null) {
                while (true) {
                    delay(500)
                    checkNfcStatus()
                    if (!isNfcEnabled) {
                        showModal = true
                    } else if (oldCardID != cardID) {
                        oldCardID = cardID
                        showScreen = true
                        lifecycleScope.launch {
                            delay(2000)
                            showScreen = false
                        }
                    }

                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            nfcViewModel.isNFCPageVisible = false
        }
    }

    Box {
        if (showScreen) {
            handleCardReaded(cardID)
        } else {
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
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Text(
                    text = "Waiting for NFC card...",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.contactless),
                    contentDescription = "NFC Icon",
                )
                Text(
                    text = "Please tap your card on the back of this device to validate.",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(20.dp)
                )
                Spacer(modifier = Modifier.padding(20.dp))
                Button(
                    onClick = {
                        navController.navigate(NavRoutes.DriverPage)
                    },
                    modifier = Modifier.padding(top = 20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50),
                        contentColor = Color.White
                    )
                ) {
                    Text("DRIVE")
                }
            }
        }
    }
}