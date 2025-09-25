package br.tmvdl.gps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import java.text.SimpleDateFormat
import java.util.*

data class LocationInfo(
    val provider: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val altitude: Double,
    val bearing: Float,
    val speed: Float,
    val timestamp: String,
    val satelliteCount: Int?,
    val isFromMockProvider: Boolean
)

data class LocationData(
    val isLocationEnabled: Boolean,
    val availableProviders: List<String>,
    val enabledProviders: List<String>,
    val currentLocations: List<LocationInfo>,
    val lastKnownLocations: List<LocationInfo>,
    val errorMessage: String?
)

class LocationDataManager(private val context: Context) {
    
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    
    val locationData = MutableLiveData<LocationData>()
    private val locationListeners = mutableMapOf<String, LocationListener>()
    private var gnssStatusCallback: GnssStatus.Callback? = null
    private var satelliteCount: Int? = null
    
    fun startLocationUpdates() {
        if (!hasLocationPermissions()) {
            updateLocationData(errorMessage = "Location permissions not granted")
            return
        }
        
        val availableProviders = locationManager.allProviders
        val enabledProviders = locationManager.getProviders(true)
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || 
                               locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        
        // Set up GNSS status callback for satellite count
        setupGnssStatusCallback()
        
        // Get last known locations
        val lastKnownLocations = getLastKnownLocations()
        
        // Start listening for location updates from all providers
        enabledProviders.forEach { provider ->
            startLocationListener(provider)
        }
        
        updateLocationData(
            isLocationEnabled = isLocationEnabled,
            availableProviders = availableProviders,
            enabledProviders = enabledProviders,
            lastKnownLocations = lastKnownLocations
        )
    }
    
    private fun setupGnssStatusCallback() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        
        gnssStatusCallback = object : GnssStatus.Callback() {
            override fun onSatelliteStatusChanged(status: GnssStatus) {
                satelliteCount = status.satelliteCount
            }
        }
        
        locationManager.registerGnssStatusCallback(gnssStatusCallback!!, null)
    }
    
    private fun startLocationListener(provider: String) {
        if (!hasLocationPermissions()) return
        
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val currentData = locationData.value ?: LocationData(
                    false, emptyList(), emptyList(), emptyList(), emptyList(), null
                )
                
                val locationInfo = LocationInfo(
                    provider = location.provider ?: provider,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    accuracy = location.accuracy,
                    altitude = location.altitude,
                    bearing = location.bearing,
                    speed = location.speed,
                    timestamp = dateFormat.format(Date(location.time)),
                    satelliteCount = if (provider == LocationManager.GPS_PROVIDER) satelliteCount else null,
                    isFromMockProvider = location.isFromMockProvider
                )
                
                val updatedCurrentLocations = currentData.currentLocations.toMutableList()
                updatedCurrentLocations.removeAll { it.provider == provider }
                updatedCurrentLocations.add(locationInfo)
                
                updateLocationData(
                    isLocationEnabled = currentData.isLocationEnabled,
                    availableProviders = currentData.availableProviders,
                    enabledProviders = currentData.enabledProviders,
                    currentLocations = updatedCurrentLocations,
                    lastKnownLocations = currentData.lastKnownLocations
                )
            }
            
            override fun onProviderEnabled(provider: String) {
                startLocationUpdates()
            }
            
            override fun onProviderDisabled(provider: String) {
                startLocationUpdates()
            }
            
            @Deprecated("Deprecated in API level 29")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                // Handle status changes if needed
            }
        }
        
        locationListeners[provider] = locationListener
        
        try {
            locationManager.requestLocationUpdates(
                provider,
                1000, // 1 second
                0f,   // 0 meters
                locationListener
            )
        } catch (e: SecurityException) {
            updateLocationData(errorMessage = "Security exception: ${e.message}")
        } catch (e: IllegalArgumentException) {
            updateLocationData(errorMessage = "Provider not available: $provider")
        }
    }
    
    private fun getLastKnownLocations(): List<LocationInfo> {
        if (!hasLocationPermissions()) return emptyList()
        
        val lastKnownLocations = mutableListOf<LocationInfo>()
        
        locationManager.allProviders.forEach { provider ->
            try {
                val lastLocation = locationManager.getLastKnownLocation(provider)
                lastLocation?.let { location ->
                    lastKnownLocations.add(
                        LocationInfo(
                            provider = location.provider ?: provider,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            accuracy = location.accuracy,
                            altitude = location.altitude,
                            bearing = location.bearing,
                            speed = location.speed,
                            timestamp = dateFormat.format(Date(location.time)),
                            satelliteCount = if (provider == LocationManager.GPS_PROVIDER) satelliteCount else null,
                            isFromMockProvider = location.isFromMockProvider
                        )
                    )
                }
            } catch (e: SecurityException) {
                // Handle permission error
            }
        }
        
        return lastKnownLocations
    }
    
    private fun updateLocationData(
        isLocationEnabled: Boolean = locationData.value?.isLocationEnabled ?: false,
        availableProviders: List<String> = locationData.value?.availableProviders ?: emptyList(),
        enabledProviders: List<String> = locationData.value?.enabledProviders ?: emptyList(),
        currentLocations: List<LocationInfo> = locationData.value?.currentLocations ?: emptyList(),
        lastKnownLocations: List<LocationInfo> = locationData.value?.lastKnownLocations ?: emptyList(),
        errorMessage: String? = null
    ) {
        locationData.postValue(
            LocationData(
                isLocationEnabled,
                availableProviders,
                enabledProviders,
                currentLocations,
                lastKnownLocations,
                errorMessage
            )
        )
    }
    
    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
               ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    
    fun stopLocationUpdates() {
        locationListeners.values.forEach { listener ->
            locationManager.removeUpdates(listener)
        }
        locationListeners.clear()
        
        gnssStatusCallback?.let { callback ->
            locationManager.unregisterGnssStatusCallback(callback)
        }
        gnssStatusCallback = null
    }
}