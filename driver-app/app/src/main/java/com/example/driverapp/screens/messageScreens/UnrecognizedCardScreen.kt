package com.example.driverapp.screens.messageScreens

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.driverapp.R

@Composable
fun UnrecognizedCardScreen(
    navController: NavController
) {
    BaseScreen(
        text = "Card not recognized",
        color = Color(0xFFFF0000),
        icon = painterResource(R.drawable.baseline_credit_card_off_24),
        secondColor = Color.White,
        navController = navController
    )
}