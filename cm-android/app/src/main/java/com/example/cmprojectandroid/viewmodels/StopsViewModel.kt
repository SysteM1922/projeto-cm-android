package com.example.cmprojectandroid.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.auth.FirebaseAuth
import com.example.cmprojectandroid.Model.Stop
import com.example.cmprojectandroid.Model.Favorite
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
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
        val database = FirebaseDatabase.getInstance().reference
        database.child("stops").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stopsList = mutableListOf<Stop>()
                for (child in snapshot.children) {
                    val stop = child.getValue(Stop::class.java)
                    if (stop != null) {
                        stopsList.add(stop)
                        Log.d("StopsViewModel", "Fetched stop: $stop")
                    }
                }
                _stops.value = stopsList
                Log.d("StopsViewModel", "Total stops fetched: ${stopsList.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("StopsViewModel", "Error fetching stops", error.toException())
            }
        })
    }

    private fun fetchFavorites() {
        val user = auth.currentUser ?: return

        firestore.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                val favoritesList = document.get("favorites") as? List<Map<String, Any>> ?: listOf()
                val favorites = favoritesList.map { map ->
                    Favorite(
                        id = map["id"] as? String ?: "",
                        name = map["name"] as? String ?: ""
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
                val favoritesList = document.get("favorites") as? List<Map<String, Any>> ?: listOf()
                val currentFavorites = favoritesList.map { map ->
                    Favorite(
                        id = map["id"] as? String ?: "",
                        name = map["name"] as? String ?: ""
                    )
                }.toMutableList()

                val existingFavorite = currentFavorites.find { it.id == stop.id }

                if (existingFavorite != null) {
                    // Remove from favorites
                    currentFavorites.remove(existingFavorite)
                    userRef.set(
                        mapOf("favorites" to currentFavorites.map { mapOf("id" to it.id, "name" to it.name) }),
                        SetOptions.merge()
                    )
                        .addOnSuccessListener {
                            _favorites.value = _favorites.value.filter { it.id != stop.id }
                            Log.d("StopsViewModel", "Stop removed from favorites: ${stop.id}")
                        }
                } else {
                    // Add to favorites
                    val newFavorite = Favorite(id = stop.id, name = stop.name)
                    currentFavorites.add(newFavorite)
                    userRef.set(
                        mapOf("favorites" to currentFavorites.map { mapOf("id" to it.id, "name" to it.name) }),
                        SetOptions.merge()
                    )
                        .addOnSuccessListener {
                            _favorites.value += newFavorite
                            Log.d("StopsViewModel", "Stop added to favorites: ${stop.id}")
                        }
                }
            } catch (e: Exception) {
                Log.e("StopsViewModel", "Error toggling favorite", e)
            }
        }
    }

    fun isStopFavorite(stopId: String): Boolean {
        return _favorites.value.any { it.id == stopId }
    }
}