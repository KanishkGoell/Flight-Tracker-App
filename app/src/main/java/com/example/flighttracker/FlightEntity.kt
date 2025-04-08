package com.example.flighttracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flights")
data class FlightEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val flightNumber: String,
    val scheduledDeparture: Long,
    val actualDeparture: Long,
    val scheduledArrival: Long,
    val actualArrival: Long,
    val dateCollected: Long
)
