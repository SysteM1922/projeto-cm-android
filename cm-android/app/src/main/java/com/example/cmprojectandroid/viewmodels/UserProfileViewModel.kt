package com.example.cmprojectandroid.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmprojectandroid.Model.Favorite
import com.example.cmprojectandroid.Model.Trip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserProfileViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favorites: StateFlow<List<Favorite>> = _favorites

    private val _tripHistory = MutableStateFlow<List<Trip>>(emptyList())
    val tripHistory: StateFlow<List<Trip>> = _tripHistory

    fun loadUserFavorites(uid: String) {
        viewModelScope.launch {
            try {
                val document = firestore.collection("users").document(uid).get().await()
                val favoritesList = document.get("favorite_stops") as? List<Map<String, Any>> ?: listOf()
                val favorites = favoritesList.map { map ->
                    Favorite(
                        stop_id = map["id"] as? String ?: "",
                        stop_name = map["name"] as? String ?: ""
                    )
                }
                _favorites.value = favorites
            } catch (e: Exception) {
                // Handle the error appropriately
            }
        }
    }

    fun removeFavorite(favorite: Favorite) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser ?: return@launch
                val userRef = firestore.collection("users").document(user.uid)

                // Remove the favorite from the local list
                val updatedFavorites = _favorites.value.toMutableList().apply {
                    removeAll { it.stop_id == favorite.stop_id }
                }
                _favorites.value = updatedFavorites

                // Update Firestore
                userRef.set(
                    mapOf("favorite_stops" to updatedFavorites.map { mapOf("id" to it.stop_id, "name" to it.stop_name) }),
                    com.google.firebase.firestore.SetOptions.merge()
                ).await()
            } catch (e: Exception) {
                // Handle errors (e.g., log them)
                e.printStackTrace()
            }
        }
    }

    fun fetchTripHistory(uid: String) {
        viewModelScope.launch {
            try {
                Log.d("UserProfileViewModel", "user uid: ${auth.currentUser?.uid}")
                val document = firestore.collection("users").document(uid).get().await()
                val tripsList = document.get("travel_history") as? List<Map<String, Any>> ?: listOf()
                val trips = tripsList.map { map ->
                    Trip(
                        trip_id = map["trip_id"] as? String ?: "",
                        trip_name = map["trip_name"] as? String ?: "",
                        timestamp = map["timestamp"] as? Long ?: 0
                    )
                }
                _tripHistory.value = trips
                Log.d("UserProfileViewModel", "Fetched ${trips} trips")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}