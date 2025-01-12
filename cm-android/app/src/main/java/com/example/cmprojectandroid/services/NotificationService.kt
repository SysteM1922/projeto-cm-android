package com.example.cmprojectandroid.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.delay

object PushNotificationManager {
    private var countReceived: Int = 0

    fun setDataReceived(count: Int) {
        this.countReceived = count
    }

    fun getDataReceived(): Int {
        return this.countReceived
    }

    suspend fun registerTokenOnServer(token: String) {
        delay(2000)
    }

}

class NotificationService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // If datastore allows it, we can receive the notification else we can ignore it
        if (PushNotificationManager.getDataReceived() == 0) {
            return
        }
        PushNotificationManager.setDataReceived(PushNotificationManager.getDataReceived() - 1)
        super.onMessageReceived(remoteMessage)
    }
}