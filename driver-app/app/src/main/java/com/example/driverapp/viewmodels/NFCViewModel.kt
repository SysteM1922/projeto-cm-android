package com.example.driverapp.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NFCViewModel : ViewModel() {
    private val _cardID = MutableLiveData<String>()
    val cardID: LiveData<String> = _cardID

    fun setCardID(cardID: String) {
        _cardID.value = cardID
    }
}