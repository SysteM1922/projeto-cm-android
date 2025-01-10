package com.example.cmprojectandroid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmprojectandroid.Model.Favorite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserProfileViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _favorites = MutableStateFlow<List<Favorite>>(emptyList())
    val favorites: StateFlow<List<Favorite>> = _favorites

    fun loadUserFavorites(uid: String) {
        viewModelScope.launch {
            try {
                val document = firestore.collection("users").document(uid).get().await()
                val favoritesList = document.get("favorites") as? List<Map<String, Any>> ?: listOf()
                val favorites = favoritesList.map { map ->
                    Favorite(
                        stop_id = map["stop_id"] as? String ?: "",
                        stop_name = map["stop_name"] as? String ?: ""
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
                    mapOf("favorites" to updatedFavorites.map { mapOf("stop_id" to it.stop_id, "stop_name" to it.stop_name) }),
                    com.google.firebase.firestore.SetOptions.merge()
                ).await()
            } catch (e: Exception) {
                // Handle errors (e.g., log them)
                e.printStackTrace()
            }
        }
    }
}