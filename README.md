### Flight Tracker Application README

#### Overview
The Flight Tracker Application is a tool that tracks real-time flight data, stores flight history, and calculates average flight durations and delays. It allows users to add and track flights, view detailed statistics, and monitor flight information on a map.

---

### Key Features

1. **Flight Tracking**: 
   - Add flight numbers to track.
   - View the status of tracked flights and their history.
   - The app fetches data from the AviationStack API and the OpenSky API.

2. **Statistics**: 
   - View average durations and delays of tracked flights.
   - Flight history is stored in a local database (Room Database).

3. **Real-Time Flight Location**: 
   - View the real-time position of tracked flights on a map using OpenSky API.

---

### Architecture

1. **Data Layer**: 
   - Room Database to store flight history and tracked flights.
   - Fetch flight data from AviationStack API (`AviationstackApi`) and OpenSky API (`OpenSkyApi`).

2. **UI Layer**:
   - **MainActivity**: Allows users to input a flight number, fetch flight data, and display the current status and location on a map.
   - **StatsActivity**: Displays tracked flight statistics (average duration, delay), and provides options to add, refresh, or delete tracked flights.

3. **Worker for Periodic Background Task**:
   - **Handler & Runnable**: Periodically fetches flight data every 24 seconds for all tracked flights, inserts the data into the local database, and updates the UI.

4. **RecyclerView**: Displays the tracked flight data (flight number, average duration, average delay).

---

### Database Structure

1. **Entities**:
   - **TrackedFlightEntity**: Stores flight numbers that are being tracked.
   - **FlightHistoryEntity**: Stores historical flight data (departure time, arrival time, scheduled times, etc.).
   - **AvgFlightTime**: Data class for calculating and storing average flight duration and delays.

2. **Room Database**:
   - **FlightDao**: Provides methods to insert, delete, and retrieve flight data.
     - `insertTrackedFlight()`
     - `getAllTrackedFlights()`
     - `insertFlightHistory()`
     - `getHistoryForFlight()`
     - `getAverageTimes()`

---

### Dependencies

1. **Retrofit**: For networking and API calls.
   - `com.squareup.retrofit2:retrofit`
   - `com.squareup.retrofit2:converter-gson`
   
2. **Room**: For local data persistence.
   - `androidx.room:room-runtime`
   - `androidx.room:room-compiler`

3. **Coroutine**: For background tasks and asynchronous operations.
   - `org.jetbrains.kotlinx:kotlinx-coroutines-core`
   - `org.jetbrains.kotlinx:kotlinx-coroutines-android`

4. **Google Maps SDK**: For displaying flight locations on a map.
   - `com.google.android.gms:play-services-maps`
   
5. **Material Components**: For UI components like `TabLayout`.
   - `com.google.android.material:material`

---

### Setup Instructions

1. **Clone the Repository**:
   - Clone the repository using `git clone <repo-url>`.

2. **Configure API Keys**:
   - You need to replace the placeholder API keys with actual API keys for `AviationStack` and `OpenSky`. Make sure you have signed up for these APIs and obtained access keys.

3. **Dependencies**:
   - Open the project in Android Studio and sync the project to download the necessary dependencies.

4. **Room Database**:
   - The app uses a local Room database to store flight tracking data. It is automatically set up on first launch.

5. **Google Maps Setup**:
   - Ensure you've set up Google Maps API by creating a project in the Google Cloud Console and getting an API key. Add this key in the `AndroidManifest.xml`.

---

### How to Use

1. **Track Flights**:
   - On the `MainActivity`, enter the flight number (e.g., `AI102`) and click the "Track Flight" button to get the status of the flight and view it on the map.

2. **View Flight Stats**:
   - In the `StatsActivity`, view average durations and delays for all tracked flights. You can refresh the stats by clicking the "Refresh Stats" button.

3. **Delete Flights**:
   - You can delete tracked flights by clicking the delete icon next to the flight in the stats view.

4. **Map View**:
   - The map shows the real-time location of the flight, including its altitude and velocity.

---


### Example API Responses

1. **AviationStack API**:
   - Returns data about the flight's scheduled and actual departure and arrival times.

2. **OpenSky API**:
   - Returns the current state of all flights in the air, including location (latitude, longitude), altitude, and velocity.

---

### Troubleshooting

- **API Errors**: Ensure that valid API keys are used for both AviationStack and OpenSky APIs. Check your API usage limits.
- **Database Issues**: If the database does not update correctly, try clearing the app data and restarting the app.
- **Map Not Showing**: Ensure that Google Maps API is correctly set up, and the API key is valid.

---

### Contribution

1. **Fork the repository**.
2. **Make your changes** and create a pull request.
3. **Write tests** for new features.
4. **Ensure the code follows the project's coding standards**.

---

### License

The code is available under the MIT License. See the [LICENSE](LICENSE) file for more details.

---

### Conclusion

This Flight Tracker app provides both manual and automated tracking of flight data, offering users a comprehensive view of flight performance, real-time location, and historical data. Feel free to contribute and extend the functionality as needed.
