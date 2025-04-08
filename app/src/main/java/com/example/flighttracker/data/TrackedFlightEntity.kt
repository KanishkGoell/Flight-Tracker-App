package com.example.flighttracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracked_flights")
data class TrackedFlightEntity(
    @PrimaryKey val flightNumber: String
)
