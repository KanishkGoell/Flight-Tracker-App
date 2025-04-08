package com.example.flighttracker.network
import com.example.flighttracker.model.AviationstackResponse

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AviationstackApi {
    @GET("flights")
    suspend fun getFlights(
        @Query("access_key") accessKey: String,
        @Query("flight_iata") flightNumber: String
    ): Response<AviationstackResponse>
}