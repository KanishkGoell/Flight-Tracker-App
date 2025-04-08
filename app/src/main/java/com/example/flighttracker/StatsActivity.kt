package com.example.flighttracker

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flighttracker.data.FlightDatabase
import com.example.flighttracker.data.FlightHistoryEntity
import com.example.flighttracker.data.TrackedFlightEntity
import com.example.flighttracker.network.AviationstackApi
import com.google.android.material.tabs.TabLayout
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class StatsActivity : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvAverageDelay: TextView
    private lateinit var btnRefresh: Button
    private lateinit var btnAdd: Button
    private lateinit var etFlightNumber: EditText
    private lateinit var adapter: FlightAdapter
    private val handler = Handler(Looper.getMainLooper())

    private val aviationApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.aviationstack.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AviationstackApi::class.java)
    }

    private val apiRunnable = object : Runnable {
        override fun run() {
            // Start background work of fetching and inserting data every 24 seconds
            fetchAndInsertFlightData()

            // Repeat the task every 24 seconds
            handler.postDelayed(this, 24000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        // Initialize views
        tabLayout = findViewById(R.id.tabLayout)
        recyclerView = findViewById(R.id.recyclerViewStats)
        tvAverageDelay = findViewById(R.id.tvAverageDelay)
        btnRefresh = findViewById(R.id.btnRefresh)
        btnAdd = findViewById(R.id.btnAddFlight)
        etFlightNumber = findViewById(R.id.etFlightNumber)
        progressBar = findViewById(R.id.progressBar) // ProgressBar

        adapter = FlightAdapter { flightNumber ->
            CoroutineScope(Dispatchers.IO).launch {
                val dao = FlightDatabase.getInstance(applicationContext).flightDao()
                dao.deleteTrackedFlight(flightNumber)
                dao.deleteFlightHistory(flightNumber)

                withContext(Dispatchers.Main) {
                    Toast.makeText(this@StatsActivity, "$flightNumber deleted", Toast.LENGTH_SHORT).show()
                    loadFlightStats()
                }
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        tabLayout.addTab(tabLayout.newTab().setText("Track Flight"))
        tabLayout.addTab(tabLayout.newTab().setText("View Stats"))
        tabLayout.getTabAt(1)?.select()

        // Handle Add Flight button click
        btnAdd.setOnClickListener {
            val flightNum = etFlightNumber.text.toString().uppercase().trim()
            if (flightNum.isEmpty()) return@setOnClickListener

            // Show ProgressBar while background task is running
            showProgressBar()

            CoroutineScope(Dispatchers.IO).launch {
                val dao = FlightDatabase.getInstance(applicationContext).flightDao()

                try {
                    val response = aviationApi.getFlights(
                        accessKey = "0ce619349b54dd59bb40e2f711737157",
                        flightNumber = flightNum
                    )

                    Log.d("StatsActivity", "🌐 API Response: ${response.code()} ${response.message()}")

                    if (response.isSuccessful) {
                        Log.d("StatsActivity", "🧾 Full response JSON: ${response.body()}")

                        val flightData = response.body()?.data?.firstOrNull {
                            it.departure?.actual != null && it.arrival?.actual != null
                        }

                        Log.d("StatsActivity", "✈️ Fetched Flight: $flightData")

                        if (flightData != null) {
                            val depTime = parseTime(flightData.departure.actual)
                            val arrTime = parseTime(flightData.arrival.actual)

                            if (depTime > 0 && arrTime > 0) {
                                dao.insertTrackedFlight(TrackedFlightEntity(flightNum))
                                dao.insertFlightHistory(
                                    FlightHistoryEntity(
                                        flightNumber = flightNum,
                                        departureAirport = flightData.departure.airport ?: "Unknown",
                                        arrivalAirport = flightData.arrival.airport ?: "Unknown",
                                        scheduledDeparture = parseTime(flightData.departure.scheduled),
                                        actualDeparture = depTime,
                                        scheduledArrival = parseTime(flightData.arrival.scheduled),
                                        actualArrival = arrTime
                                    )
                                )
                                Log.d("StatsActivity", "✅ Stored flight + history: $flightNum")
                                withContext(Dispatchers.Main) {
                                    hideProgressBar()
                                    Toast.makeText(this@StatsActivity, "Flight added", Toast.LENGTH_SHORT).show()
                                    loadFlightStats()
                                }
                            } else {
                                showInvalid()
                            }
                        } else {
                            Log.w("StatsActivity", "❌ No matching flight with valid data")
                            showInvalid()
                        }
                    } else {
                        Log.e("StatsActivity", "❌ API failed: ${response.code()} - ${response.message()}")
                        showInvalid()
                    }
                } catch (e: Exception) {
                    Log.e("StatsActivity", "❌ Exception: ${e.message}", e)
                    showInvalid()
                }
            }
        }

        // Refresh the list of tracked flights
        btnRefresh.setOnClickListener { loadFlightStats() }

        loadFlightStats()

        // Start the background task
        startPeriodicWorker()
    }

    private fun startPeriodicWorker() {
        // Start the periodic task
        handler.post(apiRunnable)
    }

    private fun stopPeriodicWorker() {
        // Stop the periodic task
        handler.removeCallbacks(apiRunnable)
    }

    private fun fetchAndInsertFlightData(specificFlightNum: String? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = FlightDatabase.getInstance(applicationContext).flightDao()

            // If a specific flight is provided, just update that one
            // Otherwise, get all tracked flights and update each one
            val flightsToUpdate = if (specificFlightNum != null) {
                listOf(specificFlightNum)
            } else {
                dao.getAllTrackedFlights().map { it.flightNumber }
            }

            for (flightNum in flightsToUpdate) {
                try {
                    val response = aviationApi.getFlights(
                        accessKey = "0ce619349b54dd59bb40e2f711737157",
                        flightNumber = flightNum
                    )

                    Log.d("StatsActivity", "🌐 API Response for $flightNum: ${response.code()} ${response.message()}")

                    if (response.isSuccessful) {
                        val flightData = response.body()?.data?.firstOrNull {
                            it.departure?.actual != null && it.arrival?.actual != null
                        }

                        if (flightData != null) {
                            val depTime = parseTime(flightData.departure.actual)
                            val arrTime = parseTime(flightData.arrival.actual)

                            if (depTime > 0 && arrTime > 0) {
                                dao.insertFlightHistory(
                                    FlightHistoryEntity(
                                        flightNumber = flightNum,
                                        departureAirport = flightData.departure.airport ?: "Unknown",
                                        arrivalAirport = flightData.arrival.airport ?: "Unknown",
                                        scheduledDeparture = parseTime(flightData.departure.scheduled),
                                        actualDeparture = depTime,
                                        scheduledArrival = parseTime(flightData.arrival.scheduled),
                                        actualArrival = arrTime
                                    )
                                )
                                Log.d("StatsActivity", "✅ Updated flight history: $flightNum")
                            }
                        } else {
                            Log.w("StatsActivity", "❌ No matching flight with valid data for $flightNum")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("StatsActivity", "❌ Exception for $flightNum: ${e.message}", e)
                }
            }

            // Update UI after batch processing
            withContext(Dispatchers.Main) {
                loadFlightStats()
            }
        }
    }
    private fun loadFlightStats() {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = FlightDatabase.getInstance(applicationContext).flightDao()
            val flights = dao.getAverageTimes()
            withContext(Dispatchers.Main) {
                adapter.submitList(flights)
            }
        }
    }

    // Show ProgressBar when background work starts
    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    // Hide ProgressBar when background work finishes
    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun parseTime(str: String?): Long {
        return try {
            if (str == null) return 0L
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
            sdf.parse(str.replace("Z", "+0000"))?.time ?: 0L
        } catch (e: Exception) {
            Log.e("StatsActivity", "Failed to parse time: $str", e)
            0L
        }
    }

    private suspend fun showInvalid() = withContext(Dispatchers.Main) {
        Toast.makeText(this@StatsActivity, "❌ Invalid flight or missing data", Toast.LENGTH_SHORT).show()
        hideProgressBar()
    }
}

