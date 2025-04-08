package com.example.flighttracker

import android.app.Application

class FlightTrackerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Schedule WorkManager job for Q2 (daily or periodic)
    }
}
