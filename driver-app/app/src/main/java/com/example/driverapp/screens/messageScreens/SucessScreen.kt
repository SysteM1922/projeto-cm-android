package com.example.driverapp.screens.messageScreens

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.driverapp.R

@Composable
fun SucessScreen (
    userName: String = "",
    navController: NavController
) {
    BaseScreen(
        text = "Success",
        color = Color(0xFF2DC42D),
        icon = painterResource(R.drawable.baseline_check_24),
        secondColor = Color.White,
        extraText = userName,
        navController = navController
    )
}
