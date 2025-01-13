package com.example.driverapp.screens.messageScreens

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.driverapp.R

@Composable
fun NoConnectionScreen () {
    BaseScreen(
        text = "No Internet Connection",
        color = Color(0xFF9F39DA),
        icon = painterResource(R.drawable.baseline_signal_wifi_off_24),
        secondColor = Color.White,
    )
}