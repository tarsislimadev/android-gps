package br.tmvdl.gps

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class LocationInfoAdapter : ListAdapter<LocationInfo, LocationInfoAdapter.LocationViewHolder>(LocationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location_info, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvProvider: TextView = itemView.findViewById(R.id.tv_provider)
        private val tvCoordinates: TextView = itemView.findViewById(R.id.tv_coordinates)
        private val tvDetails: TextView = itemView.findViewById(R.id.tv_details)
        private val tvTimestamp: TextView = itemView.findViewById(R.id.tv_timestamp)

        fun bind(locationInfo: LocationInfo) {
            tvProvider.text = "Provider: ${locationInfo.provider}"
            
            tvCoordinates.text = "Lat: ${String.format("%.6f", locationInfo.latitude)}, " +
                    "Lng: ${String.format("%.6f", locationInfo.longitude)}"
            
            val details = buildString {
                append("Accuracy: ${String.format("%.1f", locationInfo.accuracy)}m")
                
                if (locationInfo.altitude != 0.0) {
                    append("\nAltitude: ${String.format("%.1f", locationInfo.altitude)}m")
                }
                
                if (locationInfo.speed > 0) {
                    append("\nSpeed: ${String.format("%.1f", locationInfo.speed)} m/s")
                }
                
                if (locationInfo.bearing > 0) {
                    append("\nBearing: ${String.format("%.1f", locationInfo.bearing)}°")
                }
                
                locationInfo.satelliteCount?.let { count ->
                    append("\nSatellites: $count")
                }
                
                if (locationInfo.isFromMockProvider) {
                    append("\n⚠️ Mock Location")
                }
            }
            
            tvDetails.text = details
            tvTimestamp.text = "Updated: ${locationInfo.timestamp}"
        }
    }

    class LocationDiffCallback : DiffUtil.ItemCallback<LocationInfo>() {
        override fun areItemsTheSame(oldItem: LocationInfo, newItem: LocationInfo): Boolean {
            return oldItem.provider == newItem.provider
        }

        override fun areContentsTheSame(oldItem: LocationInfo, newItem: LocationInfo): Boolean {
            return oldItem == newItem
        }
    }
}