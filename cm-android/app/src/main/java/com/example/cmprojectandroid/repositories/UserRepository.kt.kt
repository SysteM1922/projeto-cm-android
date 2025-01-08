package com.example.cmprojectandroid.repositories

import android.annotation.SuppressLint
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object UserRepository {

    @SuppressLint("StaticFieldLeak")
    private val firestore = FirebaseFirestore.getInstance()

    /**
     * Creates or updates the user record in Firestore.
     * If no record exists, it creates a new one with default favorites, etc.
     */
    suspend fun upsertUserInFirestore(user: FirebaseUser) {
        val userDocRef = firestore.collection("users").document(user.uid)
        val snapshot = userDocRef.get().await()

        if (!snapshot.exists()) {
            // Create a new user doc
            val userData = mapOf(
                "uid" to user.uid,
                "displayName" to (user.displayName ?: ""),
                "email" to (user.email ?: ""),
                "favorites" to emptyList<String>() // default empty favorites
            )
            userDocRef.set(userData).await()
        } else {
            // (Optional) Update logic if you want to ensure fields remain consistent
            // e.g., userDocRef.update("email", user.email)
        }
    }
}