package com.example.cmprojectandroid.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cmprojectandroid.Model.Preference
import com.example.cmprojectandroid.Model.Stop
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PreferencesViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val stops = mutableMapOf<String, Stop>()
    private val trips = mutableMapOf<String, String>()

    private val _preferences = MutableStateFlow<Map<String, Preference>>(emptyMap())
    val preferences: StateFlow<Map<String, Preference>> = _preferences

    init {
        fetchPreferences()
    }

    private fun fetchPreferences() {
        val user = auth.currentUser ?: return

        viewModelScope.launch {
            try {
                firestore.collection("users")
                    .document(user.uid)
                    .get()
                    .addOnSuccessListener { document ->

                        val preferencesList = document.get("preferences") as? List<Map<String, Any>> ?: emptyList()

                        val fetchedPreferences = preferencesList.map { map ->
                            Preference(
                                trip_id = map["trip_id"] as? String ?: "",
                                stop_id = map["stop_id"] as? String ?: "",
                                days = map["days"] as? List<String> ?: emptyList(),
                                today = map["today"] as? String ?: "",
                                trip_short_name = "",
                                stop_name = ""
                            )
                        }

                        _preferences.value = fetchedPreferences.associateBy { it.trip_id + "/" + it.stop_id }
                        Log.d("PreferencesViewModel", "Preferences initial values set: ${_preferences.value}")

                        viewModelScope.launch {
                            val stopsDeferred = async { fetchStops() }
                            val tripsDeferred = async { fetchTrips() }

                            awaitAll(stopsDeferred, tripsDeferred)
                            updatePreferenceData()
                            Log.d("PreferencesViewModel", "Preferences updated with stop and trip data: ${_preferences.value}")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("PreferencesViewModel", "Error fetching user document: $exception")
                    }
            } catch (e: Exception) {
                Log.e("PreferencesViewModel", "An unexpected error occurred during preference fetching: $e")
            }
        }
    }

    private suspend fun fetchStops() {
        try {
            val snapshot = firestore.collection("stops").get().await()
            for (document in snapshot.documents) {
                val stop = Stop(document.id, document.getString("stop_name") ?: "")
                stops[stop.stop_id] = stop
            }
        } catch (e: Exception) {
            Log.e("PreferencesViewModel", "Error fetching stops: $e")
        }
    }

    private suspend fun fetchTrips() {
        try {
            val snapshot = firestore.collection("trips").get().await()
            for (document in snapshot.documents) {
                trips[document.id] = document.getString("trip_short_name") ?: ""
            }
        } catch (e: Exception) {
            Log.e("PreferencesViewModel", "Error fetching trips: $e")
        }
    }

    private fun updatePreferenceData() {
        val updatedPreferences = _preferences.value.mapValues { (_, preference) ->
            preference.copy(
                stop_name = stops[preference.stop_id]?.stop_name ?: "",
                trip_short_name = trips[preference.trip_id] ?: ""
            )
        }
        _preferences.value = updatedPreferences
    }

    fun getPreference(tripId: String, stopId: String): Preference? {
        if (_preferences.value.containsKey("$tripId/$stopId")) {
            return _preferences.value["$tripId/$stopId"]
        } else {
            return Preference(trip_id = tripId, stop_id = stopId, days = emptyList(), today = "")
        }
    }

    fun updatePreferences(preference: Preference) {
        val user = auth.currentUser ?: return
        var oldPreference : Preference? = null

        if (_preferences.value.containsKey(preference.trip_id + "/" + preference.stop_id)) {
            oldPreference = _preferences.value[preference.trip_id + "/" + preference.stop_id]!!
        } else {
            oldPreference = Preference(trip_id = preference.trip_id, stop_id = preference.stop_id, days = emptyList(), today = "")
        }

        updateNotificationTopics(oldPreference, preference)

        firestore.collection("users")
            .document(user.uid)
            .update(
                "preferences",
                _preferences.value.values.toList()
            )

        _preferences.value = _preferences.value.toMutableMap().apply {
            put(preference.trip_id + "/" + preference.stop_id, preference)
        }
    }

    private fun updateNotificationTopics(oldPreference: Preference, newPreference: Preference) {
        for (day in newPreference.days) {
            if (!oldPreference.days.contains(day)) {
                // Subscribe to the topic
                FirebaseMessaging.getInstance().subscribeToTopic(newPreference.trip_id + "-" + day).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("PreferencesViewModel", "Subscribed to topic: ${newPreference.trip_id}-$day")
                    } else {
                        Log.e("PreferencesViewModel", "Failed to subscribe to topic: ${newPreference.trip_id}-$day")
                        Log.e("PreferencesViewModel", it.exception.toString())
                    }
                }
            }
        }

        for (day in oldPreference.days) {
            if (!newPreference.days.contains(day)) {
                // Unsubscribe from the topic
                FirebaseMessaging.getInstance().unsubscribeFromTopic(oldPreference.trip_id + "-" + day).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("PreferencesViewModel", "Unsubscribed from topic: ${oldPreference.trip_id}-$day")
                    } else {
                        Log.e("PreferencesViewModel", "Failed to unsubscribe from topic: ${oldPreference.trip_id}-$day")
                        Log.e("PreferencesViewModel", it.exception.toString())
                    }
                }
            }
        }

        if (oldPreference.today != newPreference.today) {
            // Unsubscribe from the old topic
            if (oldPreference.today != "") {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(oldPreference.trip_id + "-" + oldPreference.today).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("PreferencesViewModel", "Unsubscribed from topic: ${oldPreference.trip_id}-${oldPreference.today}")
                    } else {
                        Log.e("PreferencesViewModel", "Failed to unsubscribe from topic: ${oldPreference.trip_id}-${oldPreference.today}")
                        Log.e("PreferencesViewModel", it.exception.toString())
                    }
                }
            }
            // Subscribe to the new topic
            if (newPreference.today != "") {
                FirebaseMessaging.getInstance().subscribeToTopic(newPreference.trip_id + "-" + newPreference.today).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("PreferencesViewModel", "Subscribed to topic: ${newPreference.trip_id}-${newPreference.today}")
                    } else {
                        Log.e("PreferencesViewModel", "Failed to subscribe to topic: ${newPreference.trip_id}-${newPreference.today}")
                        Log.e("PreferencesViewModel", it.exception.toString())
                    }
                }
            }
        }
    }

    fun subscribeAllTopics() {
        for (preference in _preferences.value.values) {
            for (day in preference.days) {
                FirebaseMessaging.getInstance().subscribeToTopic(preference.trip_id + "-" + day).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("PreferencesViewModel", "Subscribed to topic: ${preference.trip_id}-$day")
                    } else {
                        Log.e("PreferencesViewModel", "Failed to subscribe to topic: ${preference.trip_id}-$day")
                        Log.e("PreferencesViewModel", it.exception.toString())
                    }
                }
            }
            if (preference.today != "") {
                FirebaseMessaging.getInstance().subscribeToTopic(preference.trip_id + "-" + preference.today).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("PreferencesViewModel", "Subscribed to topic: ${preference.trip_id}-${preference.today}")
                    } else {
                        Log.e("PreferencesViewModel", "Failed to subscribe to topic: ${preference.trip_id}-${preference.today}")
                        Log.e("PreferencesViewModel", it.exception.toString())
                    }
                }
            }
        }
    }

    fun unsubscribeAllTopics() {
        for (preference in _preferences.value.values) {
            for (day in preference.days) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(preference.trip_id + "-" + day).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("PreferencesViewModel", "Unsubscribed from topic: ${preference.trip_id}-$day")
                    } else {
                        Log.e("PreferencesViewModel", "Failed to unsubscribe from topic: ${preference.trip_id}-$day")
                        Log.e("PreferencesViewModel", it.exception.toString())
                    }
                }
            }
            if (preference.today != "") {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(preference.trip_id + "-" + preference.today).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("PreferencesViewModel", "Unsubscribed from topic: ${preference.trip_id}-${preference.today}")
                    } else {
                        Log.e("PreferencesViewModel", "Failed to unsubscribe from topic: ${preference.trip_id}-${preference.today}")
                        Log.e("PreferencesViewModel", it.exception.toString())
                    }
                }
            }
        }
    }
}