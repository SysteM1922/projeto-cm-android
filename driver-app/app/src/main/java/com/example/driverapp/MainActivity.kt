package com.example.driverapp

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.driverapp.navigation.BottomNavItem
import com.example.driverapp.navigation.BottomNavigationBar
import com.example.driverapp.navigation.NavRoutes
import com.example.driverapp.navigation.NavigationHost
import com.example.driverapp.ui.theme.DriverAppTheme
import com.example.driverapp.viewmodels.NFCViewModel
import com.google.firebase.auth.FirebaseAuth

const val NFC_READER_OPENED = "com.example.driverapp.NFC_READER_OPENED"
const val NFC_READER_CLOSED = "com.example.driverapp.NFC_READER_CLOSED"

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private val sharedViewModel: NFCViewModel by viewModels()

    private val nfcReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                NFC_READER_OPENED -> enableNfcForegroundDispatch()
                NFC_READER_CLOSED -> disableNfcForegroundDispatch()
            }
            Log.d("MainActivity", "NFC Receiver: ${intent?.action}")
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        enableEdgeToEdge()
        setContent {
            DriverAppTheme {
                MainScreen()
            }
        }

        val filter = IntentFilter().apply {
            addAction(NFC_READER_OPENED)
            addAction(NFC_READER_CLOSED)
        }
        registerReceiver(nfcReceiver, filter)

        sendBroadcast(Intent(NFC_READER_CLOSED))
    }

    fun enableNfcForegroundDispatch() {
        nfcAdapter?.let { adapter ->
            if (adapter.isEnabled) {
                val nfcIntentFilter = arrayOf(
                    IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                    IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
                )

                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_MUTABLE
                    )
                } else {
                    PendingIntent.getActivity(
                        this,
                        0,
                        Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                }
                adapter.enableForegroundDispatch(
                    this, pendingIntent, nfcIntentFilter, null
                )
            }
        }
    }

    fun disableNfcForegroundDispatch() {
        Log.d("MainActivity", "Disabling NFC Foreground Dispatch")
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onResume() {
        Log.d("MainActivity", "onResume")
        enableNfcForegroundDispatch()
        super.onResume()
    }

    override fun onPause() {
        Log.d("MainActivity", "onPause")
        disableNfcForegroundDispatch()
        super.onPause()
    }

    override fun onNewIntent(intent: Intent) {
        Log.d("MainActivity", "New NFC Intent")

        super.onNewIntent(intent)
        val tag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
        } else {
            intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        }

        tag?.let {
            val ndef = Ndef.get(it)
            ndef?.connect()
            val ndefMessage = ndef?.ndefMessage
            ndef?.close()

            ndefMessage?.let { message ->
                for (record in message.records) {
                    val payload = String(record.payload).substring(3)
                    Log.d("NFC", "NDEF Record: $payload")
                    sharedViewModel.setCardID(payload)
                    Thread.sleep(1000)
                    sharedViewModel.setCardID("")
                    // Aqui você pode processar o payload conforme necessário
                }
            }
        }
    }

    override fun onDestroy() {
        unregisterReceiver(nfcReceiver)
        super.onDestroy()
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

    val showBottomBar = when (currentRoute) {
        NavRoutes.Login, NavRoutes.SignUp -> false
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


