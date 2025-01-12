package com.example.cmprojectandroid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmprojectandroid.Model.Driver
import com.example.cmprojectandroid.repositories.LiveBusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LiveBusViewModel(
    private val repository: LiveBusRepository = LiveBusRepository()
) : ViewModel() {

    private val _drivers = MutableStateFlow<List<Driver>>(emptyList())
    val drivers: StateFlow<List<Driver>> = _drivers

    init {
        viewModelScope.launch {
            repository.getDrivers().collect { driverList ->
                _drivers.value = driverList
            }
        }
    }
}