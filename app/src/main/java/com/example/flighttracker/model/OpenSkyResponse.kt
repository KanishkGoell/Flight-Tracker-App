package com.example.flighttracker.model

data class OpenSkyResponse(
    val time: Long,
    val states: List<List<Any>>?
)
