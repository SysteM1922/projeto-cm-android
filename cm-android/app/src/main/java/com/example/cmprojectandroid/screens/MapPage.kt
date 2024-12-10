package com.example.cmprojectandroid.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cmprojectandroid.viewmodels.TestDataViewModel

@Composable
fun MapPage() {
    val testDataViewModel: TestDataViewModel = viewModel()
    val message by testDataViewModel.message.collectAsState()

    Text(
        text = "Map Page\nReal-time message: $message",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    )
}