package com.example.flighttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flight_history")
data class FlightHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val flightNumber: String,
    val departureAirport: String,
    val arrivalAirport: String,
    val scheduledDeparture: Long,
    val actualDeparture: Long,
    val scheduledArrival: Long,
    val actualArrival: Long,
    val dateCollected: Long = System.currentTimeMillis()
)
