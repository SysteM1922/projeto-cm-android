package com.example.cmprojectandroid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmprojectandroid.repositories.TestDataRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TestDataViewModel(
    private val repository: TestDataRepository = TestDataRepository() // Or inject via Hilt
) : ViewModel() {

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getMessageFlow().collect { newMessage ->
                _message.value = newMessage
            }
        }
    }

    fun updateMessage(newMessage: String) {
        viewModelScope.launch {
            repository.updateMessage(newMessage)
        }
    }
}