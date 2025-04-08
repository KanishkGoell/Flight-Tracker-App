package com.example.flighttracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.flighttracker.data.AvgFlightTime

class FlightAdapter(
    private val onDelete: (String) -> Unit
) : ListAdapter<AvgFlightTime, FlightAdapter.FlightViewHolder>(DIFF) {

    inner class FlightViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val flightText: TextView = view.findViewById(R.id.tvFlightDetails)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.flight_item, parent, false)
        return FlightViewHolder(view)
    }
    fun formatMillis(ms: Double): String {
        val totalSeconds = (ms / 1000).toInt()
        val absSeconds = kotlin.math.abs(totalSeconds)

        val hours = absSeconds / 3600
        val minutes = (absSeconds % 3600) / 60
        val seconds = absSeconds % 60

        val sign = if (totalSeconds < 0) "" else ""
        return String.format("%s%02d:%02d:%02d", sign, hours, minutes, seconds)
    }



    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        val item = getItem(position)
        val duration = formatMillis(item.avgDuration)
        val delay = formatMillis(item.avgDelay)
        holder.flightText.text = "${item.flightNumber} ⏱ $duration (Average Delay: $delay)"

        holder.btnDelete.setOnClickListener {
            onDelete(item.flightNumber)
        }

    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<AvgFlightTime>() {
            override fun areItemsTheSame(oldItem: AvgFlightTime, newItem: AvgFlightTime) =
                oldItem.flightNumber == newItem.flightNumber

            override fun areContentsTheSame(oldItem: AvgFlightTime, newItem: AvgFlightTime) =
                oldItem == newItem
        }
    }
}

