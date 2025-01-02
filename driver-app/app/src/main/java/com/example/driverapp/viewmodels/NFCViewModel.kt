package com.example.driverapp.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NFCViewModel : ViewModel() {
    var isNFCPageVisible by mutableStateOf(false)

    var cardID = mutableStateOf("")
}