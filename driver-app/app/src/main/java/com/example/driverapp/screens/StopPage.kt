package com.example.driverapp.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.driverapp.navigation.NavRoutes
import com.example.driverapp.viewmodels.DriverViewModel

@Composable
fun StopPage(
    driverViewModel: DriverViewModel,
    navController: NavController
) {
    var stopName by remember { mutableStateOf("") }
    var arrivalTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        stopName = driverViewModel.getCurrentStopName()
        arrivalTime = driverViewModel.getCurrentStopArrivalTime()
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stopName,
            modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp
        )
        Spacer(modifier = Modifier.padding(32.dp))
        Log.d("StopPage", "Last stop: ${driverViewModel.lastStop}")
        if (driverViewModel.stopTimes.size - 1 == driverViewModel.lastStop) {
            Button(
                modifier = Modifier.padding(16.dp).fillMaxWidth().fillMaxHeight(1/3f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336),
                    contentColor = Color.White
                ),
                onClick = {
                    navController.navigate(NavRoutes.DriverPage)
                    driverViewModel.updateLastStop()
                    driverViewModel.endTrip()
                }
            ) {
                Text(
                    text = "FINISH",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    color = Color.White
                )
            }
        } else {
            Button(
                modifier = Modifier.padding(16.dp).fillMaxWidth().fillMaxHeight(1/3f),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50),
                    contentColor = Color.White
                ),
                onClick = {
                    navController.navigate(NavRoutes.NFCPage)
                    driverViewModel.sendArrivalNotification(stopName)
                }
            ) {
                Text(
                    text = "ARRIVED",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp,
                    color = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.padding(32.dp))
        Text(
            text = "Expected arrival time:",
            fontSize = 16.sp
        )
        Text(
            text = arrivalTime,
            fontSize = 24.sp
        )
    }
}