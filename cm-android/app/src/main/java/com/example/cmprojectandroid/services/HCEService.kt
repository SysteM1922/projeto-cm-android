package com.example.cmprojectandroid.services

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle

class HCEService : HostApduService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("Service started")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        println("Received APDU: " + commandApdu?.contentToString())
        return byteArrayOf()
    }

    override fun onDeactivated(reason: Int) {
        println("Not yet implemented")
    }
}