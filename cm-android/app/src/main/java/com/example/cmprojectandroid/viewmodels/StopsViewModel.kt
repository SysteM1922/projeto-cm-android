package com.example.cmprojectandroid.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.auth.FirebaseAuth
import com.example.cmprojectandroid.Model.Stop
import com.example.cmprojectandroid.Model.Favorite
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class StopsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _stops = mutableStateOf<List<Stop>>(emptyList())
    val stops: State<List<Stop>> = _stops

    // Change favorites to a list of Favorite objects
    private val _favorites = mutableStateOf<List<Favorite>>(emptyList())
    val favorites: State<List<Favorite>> = _favorites

    init {
        fetchStops()
        fetchFavorites()
    }

    private fun fetchStops() {
        viewModelScope.launch {
            try {
                val stopsList = mutableListOf<Stop>()
                val querySnapshot = firestore.collection("stops").get().await()
                for (document in querySnapshot.documents) {
                    val stop = document.toObject(Stop::class.java)
                    if (stop != null) {
                        stopsList.add(stop)
                        Log.d("StopsViewModel", "Fetched stop: $stop")
                    }
                }
                _stops.value = stopsList
                Log.d("StopsViewModel", "Total stops fetched: ${stopsList.size}")
            } catch (e: Exception) {
                Log.e("StopsViewModel", "Error fetching stops", e)
            }
        }
    }

    private fun fetchFavorites() {
        val user = auth.currentUser ?: return

        firestore.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                val favoritesList = document.get("favorite_stops") as? List<Map<String, Any>> ?: listOf()
                val favorites = favoritesList.map { map ->
                    Favorite(
                        stop_id = map["id"] as? String ?: "",
                        stop_name = map["name"] as? String ?: ""
                    )
                }
                _favorites.value = favorites
            }
            .addOnFailureListener { e ->
                Log.e("StopsViewModel", "Error fetching favorites", e)
            }
    }

    fun toggleFavorite(stop: Stop) {
        viewModelScope.launch {
            try {
                val user = auth.currentUser
                if (user == null) {
                    Log.e("StopsViewModel", "User not authenticated")
                    return@launch
                }

                val userRef = firestore.collection("users").document(user.uid)

                // Fetch current favorites
                val document = userRef.get().await()
                val favoritesList = document.get("favorite_stops") as? List<Map<String, Any>> ?: listOf()
                val currentFavorites = favoritesList.map { map ->
                    Favorite(
                        stop_id = map["id"] as? String ?: "",
                        stop_name = map["name"] as? String ?: ""
                    )
                }.toMutableList()

                val existingFavorite = currentFavorites.find { it.stop_id == stop.stop_id }

                if (existingFavorite != null) {
                    // Remove from favorites
                    currentFavorites.remove(existingFavorite)
                    userRef.set(
                        mapOf("favorite_stops" to currentFavorites.map { mapOf("id" to it.stop_id, "name" to it.stop_name) }),
                        SetOptions.merge()
                    )
                        .addOnSuccessListener {
                            _favorites.value = _favorites.value.filter { it.stop_id != stop.stop_id }
                            Log.d("StopsViewModel", "Stop removed from favorites: ${stop.stop_id}")
                        }
                } else {
                    // Add to favorites
                    val newFavorite = Favorite(stop_id = stop.stop_id, stop_name = stop.stop_name)
                    currentFavorites.add(newFavorite)
                    userRef.set(
                        mapOf("favorite_stops" to currentFavorites.map { mapOf("id" to it.stop_id, "name" to it.stop_name) }),
                        SetOptions.merge()
                    )
                        .addOnSuccessListener {
                            _favorites.value += newFavorite
                            Log.d("StopsViewModel", "Stop added to favorites: ${stop.stop_id}")
                        }
                }
            } catch (e: Exception) {
                Log.e("StopsViewModel", "Error toggling favorite", e)
            }
        }
    }

}