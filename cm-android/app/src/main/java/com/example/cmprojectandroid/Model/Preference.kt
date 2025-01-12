package com.example.cmprojectandroid.Model

data class Preference (
    val trip_id: String = "",
    var trip_short_name: String = "",
    val stop_id: String = "",
    var stop_name: String = "",
    var days: List<String> = emptyList(),
    var today: String = ""
)