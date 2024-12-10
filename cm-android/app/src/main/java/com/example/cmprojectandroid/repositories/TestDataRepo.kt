package com.example.cmprojectandroid.repositories

import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class TestDataRepository {
    private val databaseRef = FirebaseDatabase.getInstance().reference.child("testData")

    fun getMessageFlow(): Flow<String> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val message = snapshot.child("message").getValue(String::class.java) ?: ""
                trySend(message).isSuccess
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        databaseRef.addValueEventListener(listener)
        awaitClose { databaseRef.removeEventListener(listener) }
    }

    suspend fun updateMessage(newMessage: String) {
        databaseRef.child("message").setValue(newMessage)
    }
}