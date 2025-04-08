package com.example.flighttracker.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FlightDao {

    // Tracked flights
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackedFlight(flight: TrackedFlightEntity)

    @Query("SELECT * FROM tracked_flights")
    suspend fun getAllTrackedFlights(): List<TrackedFlightEntity>

    // Flight history
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlightHistory(flight: FlightHistoryEntity)



    @Query("SELECT * FROM flight_history WHERE flightNumber = :flight")
    suspend fun getHistoryForFlight(flight: String): List<FlightHistoryEntity>

    @Query("DELETE FROM tracked_flights WHERE flightNumber = :flightNumber")
    suspend fun deleteTrackedFlight(flightNumber: String)

    @Query("DELETE FROM flight_history WHERE flightNumber = :flightNumber")
    suspend fun deleteFlightHistory(flightNumber: String)


    @Query("""
    SELECT flightNumber, 
           AVG(actualArrival - actualDeparture) as avgDuration,
           AVG((actualArrival - scheduledArrival)) as avgDelay
    FROM flight_history 
    GROUP BY flightNumber
""")

    suspend fun getAverageTimes(): List<AvgFlightTime>




}

data class AvgFlightTime(
    val flightNumber: String,
    val avgDuration: Double,
    val avgDelay: Double
)

