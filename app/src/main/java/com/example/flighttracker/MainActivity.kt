package com.example.flighttracker

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.flighttracker.model.OpenSkyResponse
import com.example.flighttracker.network.OpenSkyApi
import com.google.android.material.tabs.TabLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var etFlightNumber: EditText
    private lateinit var tvFlightInfo: TextView
    private lateinit var mapView: MapView
    private lateinit var tabLayout: TabLayout
    private lateinit var btnTrackFlight: Button

    private var googleMap: GoogleMap? = null

    private lateinit var openSkyApi: OpenSkyApi

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnTrackFlight = findViewById(R.id.btnTrackFlight)

        btnTrackFlight.setOnClickListener {
            val flightNumber = etFlightNumber.text.toString().trim().uppercase()
            if (flightNumber.isEmpty()) {
                Toast.makeText(this@MainActivity, "Please enter a valid flight number", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    fetchAndDisplayFlight(flightNumber)
                }
            }
        }

        etFlightNumber = findViewById(R.id.etFlightNumber)
        tvFlightInfo = findViewById(R.id.tvFlightInfo)
        mapView = findViewById(R.id.mapView)
        tabLayout = findViewById(R.id.tabLayout)
        // Add tabs to TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Track Flight"))
        tabLayout.addTab(tabLayout.newTab().setText("View Stats"))
        tabLayout.getTabAt(0)?.select() // Optional: preselect the first tab



        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        openSkyApi = Retrofit.Builder()
            .baseUrl("https://opensky-network.org/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenSkyApi::class.java)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        // Do nothing – stay on Track Flight screen
                    }
                    1 -> {
                        startActivity(Intent(this@MainActivity, StatsActivity::class.java))
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    private fun formatFlightData(flight: List<Any>): String {
        val callsign = flight.getOrNull(1)?.toString()?.trim() ?: "N/A"
        val country = flight.getOrNull(2)?.toString() ?: "N/A"
        val longitude = flight.getOrNull(5)?.toString() ?: "N/A"
        val latitude = flight.getOrNull(6)?.toString() ?: "N/A"
        val altitude = flight.getOrNull(7)?.toString() ?: "N/A"
        val onGround = flight.getOrNull(8)?.toString() ?: "N/A"
        val velocity = flight.getOrNull(9)?.toString() ?: "N/A"
        val heading = flight.getOrNull(10)?.toString() ?: "N/A"
        val squawk = flight.getOrNull(14)?.toString() ?: "N/A"

        return """
        ✈️  Flight: $callsign
        🌍 Country: $country
        🤭 Heading: $heading°
        📍 Lat: $latitude, Lon: $longitude
        🛬 Altitude: $altitude m
        🤨 Velocity: $velocity m/s
        🛂 Squawk Code: $squawk
        🧱 On Ground: $onGround
        """.trimIndent()
    }

    private suspend fun fetchAndDisplayFlight(flightNumber: String) {
        try {
            val response = openSkyApi.getAllFlights()
            if (response.isSuccessful) {
                val flights = response.body()?.states
                val flight = flights?.firstOrNull {
                    it.getOrNull(1)?.toString()?.trim()?.replace(" ", "") == flightNumber
                }

                if (flight != null) {
                    val info = formatFlightData(flight)
                    tvFlightInfo.text = info

                    val lat = (flight[6] as? Double) ?: return
                    val lon = (flight[5] as? Double) ?: return
                    val heading = (flight[10] as? Double) ?: 0.0

                    googleMap?.clear()
                    val planePosition = LatLng(lat, lon)
                    val originalBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_plane)
                    val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 80, 80, false)
                    val planeIcon: BitmapDescriptor = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

                    googleMap?.addMarker(
                        MarkerOptions()
                            .position(planePosition)
                            .rotation(heading.toFloat())
                            .anchor(0.5f, 0.5f)
                            .flat(true)
                            .icon(planeIcon)
                    )
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(planePosition, 6f))

                } else {
                    tvFlightInfo.text = "Flight not found in OpenSky."
                    googleMap?.clear()
                }

            } else {
                tvFlightInfo.text = "API call failed: ${response.code()}"
                googleMap?.clear()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            tvFlightInfo.text = "Error fetching flight data."
            googleMap?.clear()
        }
    }

    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { mapView.onPause(); super.onPause() }
    override fun onDestroy() { mapView.onDestroy(); super.onDestroy() }
    override fun onLowMemory() { mapView.onLowMemory(); super.onLowMemory() }
}
