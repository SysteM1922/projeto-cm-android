package com.example.driverapp.screens.messageScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.driverapp.navigation.NavRoutes
import kotlinx.coroutines.delay

@Composable
fun BaseScreen(
    text: String,
    color: Color,
    icon: Painter,
    secondColor: Color,
    extraText: String = "",
    navController: NavController
) {
    LaunchedEffect(Unit) {
        delay(2000)
        navController.navigate(NavRoutes.NFCPage)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (extraText.isNotEmpty()) {
            Text(
                text = extraText,
                color = secondColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(modifier = Modifier.padding(20.dp))
        Icon(
            painter = icon,
            contentDescription = "Warning",
            modifier = Modifier.size(200.dp),
            tint = secondColor,
        )
        Spacer(modifier = Modifier.padding(20.dp))
        Text(
            text = text,
            color = secondColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.W900,
        )
    }
}