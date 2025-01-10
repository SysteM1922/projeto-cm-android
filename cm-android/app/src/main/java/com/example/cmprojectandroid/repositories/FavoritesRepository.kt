package com.example.cmprojectandroid.repositories

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.cmprojectandroid.Model.Stop
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun toggleFavorite(stop: Stop): Result<Boolean> {
        return try {
            val user = auth.currentUser ?: return Result.failure<Boolean>(Exception("User not authenticated"))

            val userRef = firestore.collection("users").document(user.uid)

            // First check if the stop is already in favorites
            val document = userRef.get().await()
            val favorites = document.get("favorites") as? List<String> ?: listOf()

            if (favorites.contains(stop.stop_id)) {
                // Remove from favorites
                userRef.update("favorites", FieldValue.arrayRemove(stop.stop_id)).await()
                Result.success(false) // false indicates removed from favorites
            } else {
                // Add to favorites
                userRef.update("favorites", FieldValue.arrayUnion(stop.stop_id)).await()
                Result.success(true) // true indicates added to favorites
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isStopFavorited(stopId: String): Boolean {
        return try {
            val user = auth.currentUser ?: return false
            val document = firestore.collection("users")
                .document(user.uid)
                .get()
                .await()

            val favorites = document.get("favorites") as? List<String> ?: listOf()
            favorites.contains(stopId)
        } catch (e: Exception) {
            false
        }
    }
}

// StopsViewModel.kt
class StopsViewModel : ViewModel() {
    private val favoritesRepository = FavoritesRepository()
    private val _favoriteStops = MutableStateFlow<Set<String>>(setOf())
    val favoriteStops: StateFlow<Set<String>> = _favoriteStops.asStateFlow()

    fun toggleFavorite(stop: Stop) {
        viewModelScope.launch {
            favoritesRepository.toggleFavorite(stop)
                .onSuccess { isFavorite ->
                    // Update the local state
                    _favoriteStops.update { currentFavorites ->
                        if (isFavorite) {
                            currentFavorites + stop.stop_id
                        } else {
                            currentFavorites - stop.stop_id
                        }
                    }
                }
                .onFailure { exception ->
                    // Handle error (you might want to expose this to the UI)
                    Log.e("StopsViewModel", "Error toggling favorite", exception)
                }
        }
    }

    // Call this when initializing the ViewModel
    fun loadFavorites(stopId: String) {
        viewModelScope.launch {
            val isFavorite = favoritesRepository.isStopFavorited(stopId)
            if (isFavorite) {
                _favoriteStops.update { it + stopId }
            }
        }
    }
}