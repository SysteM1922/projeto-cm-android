package com.example.cmprojectandroid.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.cmprojectandroid.Model.Preference
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class PreferencesViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _preferences = mutableStateOf<Map<String, Preference>>(emptyMap())

    init {
        fetchPreferences()
    }

    private fun fetchPreferences() {
        val user = auth.currentUser ?: return

        firestore.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                val preferencesList =
                    document.get("preferences") as? List<Map<String, Any>> ?: listOf()
                val preferences = preferencesList.map { map ->
                    Preference(
                        trip_id = map["trip_id"] as? String ?: "",
                        stop_id = map["stop_id"] as? String ?: "",
                        days = map["days"] as? List<String> ?: emptyList(),
                        today = map["today"] as? String ?: ""
                    )
                }.associateBy { it.trip_id + "/" + it.stop_id }
                _preferences.value = preferences
            }
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