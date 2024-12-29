package com.example.driverapp.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*
import com.example.driverapp.Model.Stop

class StopsViewModel : ViewModel() {

    private val _stops = mutableStateOf<List<Stop>>(emptyList())
    val stops: State<List<Stop>> = _stops

    init {
        fetchStops()
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
}