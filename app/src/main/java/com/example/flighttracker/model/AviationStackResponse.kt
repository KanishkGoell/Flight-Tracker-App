package com.example.flighttracker.model

import com.google.gson.annotations.SerializedName

data class AviationstackResponse(
    @SerializedName("data") val data: List<FlightData>
)

data class FlightData(
    @SerializedName("flight") val flight: FlightNumber,
    @SerializedName("departure") val departure: AirportTime,
    @SerializedName("arrival") val arrival: AirportTime
)

data class FlightNumber(
    @SerializedName("iata") val iata: String
)

data class AirportTime(
    @SerializedName("airport") val airport: String?,
    @SerializedName("iata") val iata: String?,  // ✅ add this line
    @SerializedName("scheduled") val scheduled: String?,
    @SerializedName("actual") val actual: String?
)
