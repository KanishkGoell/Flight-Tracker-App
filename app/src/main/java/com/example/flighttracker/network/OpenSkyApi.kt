package com.example.flighttracker.network

import com.example.flighttracker.model.OpenSkyResponse
import retrofit2.Response
import retrofit2.http.GET

interface OpenSkyApi {
    @GET("states/all")
    suspend fun getAllFlights(): Response<OpenSkyResponse>
}
