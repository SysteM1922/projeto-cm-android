package com.example.driverapp.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.driverapp.Model.StopTime
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.properties.Delegates

class DriverViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var docRef: DocumentReference

    var actualTrip = ""
    var routeId = ""
    var tripName = ""
    var tripColor = ""
    var lastStop = 0
    private val _stopTimes = mutableStateListOf<StopTime>()
    val stopTimes: List<StopTime> get() = _stopTimes

    init {
        Log.d("DriverViewModel", "DriverViewModel created")
    }

    suspend fun fetchDriverData(driverId: String?): String {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                docRef = firestore.collection("drivers").document(driverId!!)
                docRef.get()
                    .addOnSuccessListener { document ->
                        Log.d("DriverViewModel", "DocumentSnapshot data: ${document.data}")
                        if (document != null) {
                            if (document.data?.get("actualTrip") != null) {
                                Log.d("DriverViewModel", "DocumentSnapshot data: ${document.data}")
                                actualTrip = document.data?.get("actualTrip").toString()
                                if (document.data?.get("lastStop") != null) {
                                    lastStop = document.data?.get("lastStop").toString().toInt()
                                }
                                if (actualTrip.isNotEmpty()) {
                                    firestore.collection("trips").document(actualTrip).get()
                                        .addOnSuccessListener { doc ->
                                            tripName = doc.data?.get("trip_short_name").toString()
                                            routeId = doc.data?.get("route_id").toString()
                                            firestore.collection("routes")
                                                .document(routeId).get()
                                                .addOnSuccessListener { route ->
                                                    tripColor =
                                                        route.data?.get("route_color").toString()

                                                    Log.d("DriverViewModel", "Trip name: $tripName")
                                                    Log.d("DriverViewModel", "Trip color: $tripColor")
                                                    continuation.resume(actualTrip)
                                                }
                                        }
                                        .addOnFailureListener { e ->
                                            Log.w("DriverViewModel", "Error getting document", e)
                                            continuation.resumeWithException(e)
                                        }
                                } else {
                                    continuation.resume(actualTrip)
                                }
                            }
                            Log.d("DriverViewModel", "Actual trip: $actualTrip")
                        } else {
                            Log.d("DriverViewModel", "No such document")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("DriverViewModel", "get failed with ", exception)
                        continuation.resumeWithException(exception)
                    }
            }
        }
    }

    private suspend fun fetchStops() {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                Log.d("DriverViewModel", "Fetching stops for trip_id: $actualTrip")
                firestore.collection("stop_times")
                    .whereEqualTo("trip_id", actualTrip)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            _stopTimes.add(
                                StopTime(
                                    document.data["arrival_time"].toString(),
                                    document.data["departure_time"].toString(),
                                    document.data["stop_id"].toString(),
                                    document.data["stop_sequence"].toString().toInt(),
                                    document.data["trip_id"].toString(),
                                )
                            )
                        }
                        _stopTimes.sortBy { it.stopSequence }
                        continuation.resume(Unit)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("DriverViewModel", "Error getting documents: ", exception)
                        continuation.resumeWithException(exception)
                    }
            }
        }
    }

    suspend fun isTripValid(tripId: String): Boolean {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                firestore.collection("trips").document(tripId).get()
                    .addOnSuccessListener { document ->
                        if (document.data.isNullOrEmpty()) {
                            Log.d("DriverViewModel", "Trip not found")
                            continuation.resume(false)
                        } else {
                            Log.d("DriverViewModel", "Trip found")
                            continuation.resume(true)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("DriverViewModel", "Error getting documents: ", exception)
                        continuation.resumeWithException(exception)
                    }
            }
        }
    }

    suspend fun startTrip(tripId: String) {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine<Void?> { continuation ->
                Log.d("DriverViewModel", "Starting trip: $tripId")
                actualTrip = tripId
                docRef.update("actualTrip", tripId)
                    .addOnSuccessListener {
                        Log.d("DriverViewModel", "DocumentSnapshot successfully updated!")
                        docRef.update("lastStop", 0)
                            .addOnSuccessListener {
                                Log.d("DriverViewModel", "DocumentSnapshot successfully updated! end")
                                firestore.collection("trips").document(tripId).get()
                                    .addOnSuccessListener { document ->
                                        tripName = document.data?.get("trip_short_name").toString()
                                        routeId = document.data?.get("route_id").toString()
                                        firestore.collection("routes")
                                            .document(routeId).get().addOnSuccessListener { route ->
                                                tripColor = route.data?.get("route_color").toString()
                                                Log.d("DriverViewModel", "Trip name: $tripName")
                                                Log.d("DriverViewModel", "Trip color: $tripColor")
                                                continuation.resume(null)
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w("DriverViewModel", "Error getting document", e)
                                        continuation.resumeWithException(e)
                                    }
                            }
                            .addOnFailureListener { e ->
                                Log.w("DriverViewModel", "Error updating document", e)
                                continuation.resumeWithException(e)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.w("DriverViewModel", "Error updating document", e)
                        continuation.resumeWithException(e)
                    }
            }
            fetchStops()
        }
    }

    fun endTrip() {
        this.actualTrip = ""
        this.routeId = ""
        this.tripName = ""
        this.tripColor = ""
        _stopTimes.clear()
        this.lastStop = 0
        docRef.update("actualTrip", null)
            .addOnSuccessListener {
                Log.d("DriverViewModel", "DocumentSnapshot successfully updated! end")
            }
            .addOnFailureListener { e ->
                Log.w("DriverViewModel", "Error updating document", e)
            }
        docRef.update("lastStop", null)
            .addOnSuccessListener {
                Log.d("DriverViewModel", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("DriverViewModel", "Error updating document", e)
            }
    }

    private fun addToTravelHistory(cardId: String) {
        // add travel to travel history array
        val travel = hashMapOf(
            "trip_id" to actualTrip,
            "trip_name" to tripName,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("users").document(cardId)
            .update("travel_history", FieldValue.arrayUnion(travel))
            .addOnSuccessListener {
                Log.d("DriverViewModel", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("DriverViewModel", "Error updating document", e)
            }
    }

    suspend fun validateCard(cardId: String): String {
        // verify if cardID is in users collection
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                firestore.collection("users").document(cardId).get()
                    .addOnSuccessListener { documents ->
                        if (documents.data.isNullOrEmpty()) {
                            Log.d("DriverViewModel", "Card not found")
                            continuation.resume("")
                        } else {
                            Log.d("DriverViewModel", "Card found")
                            addToTravelHistory(cardId)
                            continuation.resume(documents.data?.get("user_name").toString())
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.d("DriverViewModel", "Error getting documents: ", exception)
                        continuation.resumeWithException(exception)
                    }
            }
        }
    }

    fun updateLastStop() {
        this.lastStop++
        docRef.update("lastStop", lastStop)
            .addOnSuccessListener {
                Log.d("DriverViewModel", "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w("DriverViewModel", "Error updating document", e)
            }
    }

    suspend fun getCurrentStopName(): String {

        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { continuation ->
                var stopName = ""
                Log.d(
                    "DriverViewModel",
                    "Fetching stop name for stop_id: ${stopTimes[lastStop].stopId}"
                )
                firestore.collection("stops")
                    .whereEqualTo("stop_id", stopTimes[lastStop].stopId)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            Log.d("DriverViewModel", "Stop not found")
                        } else {
                            Log.d("DriverViewModel", "Stop found")
                            stopName = documents.first().data["stop_name"].toString()
                        }
                        continuation.resume(stopName)
                    }
                    .addOnFailureListener { exception ->
                        Log.d("DriverViewModel", "Error getting documents: ", exception)
                        continuation.resumeWithException(exception)
                    }
            }
        }
    }

    fun getCurrentStopArrivalTime(): String {
        return stopTimes[lastStop].arrivalTime
    }

    private val functions = FirebaseFunctions.getInstance()

    fun sendArrivalNotification(stopName: String) {
        val calendar = Calendar.getInstance()

        // Get day of week (Mon, Tue, etc.)
        val dayOfWeek = SimpleDateFormat("EEE", Locale.ENGLISH).format(calendar.time)

        // Get date in dd-MM-yyyy format
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(calendar.time)

        val topics = listOf(
            "$actualTrip-$dayOfWeek",
            "$actualTrip-$dateFormat"
        )

        topics.forEach { topic ->


            val message = mapOf(
                "notification" to mapOf(
                    "title" to "Bus Arrival Update",
                    "body" to mapOf(
                        "body" to "Bus $tripName arrived at $stopName",
                        "stopSequence" to lastStop
                    )
                ),
                "topic" to topic
            )

            Log.d("DriverViewModel", "Sending message: $message")

            // Call Firebase Cloud Function to send notifications
            functions
                .getHttpsCallable("sendMultiTopicNotification")
                .call(message)
                .addOnSuccessListener {
                    Log.d("DriverViewModel", "Notification sent successfully to topic: $topic")
                }
                .addOnFailureListener { e ->
                    Log.e("DriverViewModel", "Error sending notification to topic: $topic", e)
                    Log.e("DriverViewModel", "Error details:", e)
                }
        }
    }
}