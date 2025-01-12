package com.example.driverapp.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.driverapp.Model.InitRealTimeLocation
import com.example.driverapp.Model.RealtimeLocation
import com.example.driverapp.repositories.LocationRepo
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LocationService : Service() {

    private val locationRepo = LocationRepo()
    private val scope = CoroutineScope(Dispatchers.IO)

    companion object{
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        Log.d("LocationService", "Service started")
        startForegroundService()
    }

    private fun stop() {
        Log.d("LocationService", "Service stopped")
        stopForeground(true)
        stopSelf()
    }

    private fun startForegroundService() {

        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, "LocationServiceChannel")
            .setContentTitle("BusTracker Validator Location Service")
            .setContentText("Updating location in background")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()

        scope.launch {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@LocationService)

            val initRealTimeLocation = InitRealTimeLocation(
                bus_id = "LAWRA-123",
                bus_name = "Bus Nig",
                bus_color = "Blue"
            )
            locationRepo.initLocation(initRealTimeLocation)

            while (true) {
                try {
                    val location: Location? = fusedLocationClient.lastLocation.await()
                    location?.let {
                        val newLocation = RealtimeLocation(
                            lat = it.latitude,
                            lng = it.longitude
                        )
                        locationRepo.updateLocation(newLocation)
                        Log.d("LocationUpdateService", "Location updated: $newLocation")
                    }
                } catch (e: SecurityException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(2000) // Wait 2 seconds before next update
            }
        }

        startForeground(1, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "LocationServiceChannel",
                "Location Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}