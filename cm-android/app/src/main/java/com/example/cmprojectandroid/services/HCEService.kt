package com.example.cmprojectandroid.services

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class HCEService : HostApduService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("HCE", "Serviço criado")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("HCE", "Serviço iniciado")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray? {
        Log.d("HCE", "Comando recebido: $commandApdu")
        if (commandApdu == null) {
            return null
        }

        val command = commandApdu.toHexString()
        Log.d("HCE", "Comando recebido: $command")

        // Aqui você processa o comando APDU e retorna a resposta.
        // Exemplo: responder a um SELECT APDU
        if (command.startsWith("00A40400")) {
            val aid = command.substring(10, command.length -2)
            if (aid == "A0000000041010") {
                Log.d("HCE", "AID Selecionado A0000000041010")
                return "9000".toByteArray() // Resposta OK
            } else if (aid == "A0000000042010") {
                Log.d("HCE", "AID Selecionado A0000000042010")
                return "9000".toByteArray()
            }
            return "6A82".toByteArray() // Arquivo não encontrado
        }

        return "6A82".toByteArray() // Comando não suportado
    }

    override fun onDeactivated(reason: Int) {
        Log.d("HCE", "Serviço desativado: $reason")
    }

    fun ByteArray.toHexString() : String = joinToString("") { String.format("%02X", it) }
}