package com.example.driverapp

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.driverapp.navigation.NavRoutes
import com.example.driverapp.navigation.NavigationHost
import com.example.driverapp.ui.theme.DriverAppTheme
import com.example.driverapp.viewmodels.DriverViewModel
import com.example.driverapp.viewmodels.NFCViewModel
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException

class MainActivity : ComponentActivity(), NfcAdapter.ReaderCallback {

    val sharedViewModel: NFCViewModel by viewModels()
    var nfcAdapter: NfcAdapter? = null
    val driverViewModel: DriverViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        enableEdgeToEdge()
        setContent {
            DriverAppTheme {
                MainScreen(
                    nfcViewModel = sharedViewModel,
                    driverViewModel = driverViewModel
                )
            }
        }
        requestedOrientation = SCREEN_ORIENTATION_PORTRAIT
    }

    override fun onResume() {
        super.onResume()
        if (nfcAdapter != null) {
            val options = Bundle()
            // Work around for some broken Nfc firmware implementations that poll the card too fast
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)

            // Enable ReaderMode for all types of card and disable platform sounds
            nfcAdapter!!.enableReaderMode(
                this,
                this,
                NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B or
                        NfcAdapter.FLAG_READER_NFC_F or
                        NfcAdapter.FLAG_READER_NFC_V or
                        NfcAdapter.FLAG_READER_NFC_BARCODE or
                        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                options
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (nfcAdapter != null) nfcAdapter!!.disableReaderMode(this)
    }

    override fun onTagDiscovered(tag: Tag?) {
        if (!sharedViewModel.isNFCPageVisible) {
            return
        }
        sharedViewModel.cardID.value = ""
        val ndef = Ndef.get(tag)
        if (ndef != null) {
            try {
                ndef.connect()
                // Check if the tag is NDEF formatted
                if (ndef.isConnected) {
                    val ndefMessage = ndef.ndefMessage
                    parserNDEFMessage(ndefMessage)
                } else {
                    // Tag is not NDEF formatted
                    runOnUiThread {
                        Toast.makeText(
                            applicationContext,
                            "NFC Tag is not NDEF formatted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: IOException) {
                // Handle I/O exception
                e.printStackTrace()
            } catch (e: FormatException) {
                // Handle FormatException
                e.printStackTrace()
            } finally {
                // Close the connection
                try {
                    ndef.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun parserNDEFMessage(messages: NdefMessage) {
        val cardID = String(messages.records[0].payload, charset("UTF-8")).substring(3)
        Log.d("ReadNFCActivity", "Card ID: $cardID")
        sharedViewModel.cardID.value = cardID
    }
}


object NFCParser {
    // A utility function that extracts the card ID from an NDEF message
    fun parseCardIDFromNdefMessage(ndefMessage: NdefMessage): String {
        val payload = String(ndefMessage.records[0].payload, Charsets.UTF_8)
        // Example: skipping the first 3 chars for language code (e.g. "en")
        // You can adjust this to match your actual NFC data format.
        return payload.substring(3)
    }
}


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MainScreen(
    nfcViewModel: NFCViewModel,
    driverViewModel: DriverViewModel
) {
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            // User is logged in, navigate to Driver page
            navController.navigate(NavRoutes.DriverPage) {
                popUpTo(0)
            }
        } else {
            // User is not logged in, navigate to Login page
            navController.navigate(NavRoutes.Login) {
                popUpTo(0)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        NavigationHost(navController, nfcViewModel, driverViewModel, modifier = Modifier.padding(innerPadding))
    }
}


