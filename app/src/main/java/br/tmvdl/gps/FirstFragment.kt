package br.tmvdl.gps

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import br.tmvdl.gps.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var locationDataManager: LocationDataManager
    private lateinit var currentLocationsAdapter: LocationInfoAdapter
    private lateinit var lastKnownLocationsAdapter: LocationInfoAdapter

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Precise location access granted.
                startLocationTracking()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Only approximate location access granted.
                startLocationTracking()
            }
            else -> {
                // No location access granted.
                showError("Location permissions denied. Please grant location permissions to use this app.")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupLocationManager()
        
        checkPermissionsAndStartTracking()
    }

    private fun setupRecyclerViews() {
        currentLocationsAdapter = LocationInfoAdapter()
        lastKnownLocationsAdapter = LocationInfoAdapter()

        binding.rvCurrentLocations.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = currentLocationsAdapter
        }

        binding.rvLastKnownLocations.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = lastKnownLocationsAdapter
        }
    }

    private fun setupLocationManager() {
        locationDataManager = LocationDataManager(requireContext())
        locationDataManager.locationData.observe(viewLifecycleOwner) { data ->
            updateUI(data)
        }
    }

    private fun checkPermissionsAndStartTracking() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startLocationTracking()
            }
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                startLocationTracking()
            }
            else -> {
                locationPermissionRequest.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }
        }
    }

    private fun startLocationTracking() {
        locationDataManager.startLocationUpdates()
    }

    private fun updateUI(data: LocationData) {
        // Update system status
        binding.tvLocationEnabled.text = "Location Services: ${if (data.isLocationEnabled) "Enabled" else "Disabled"}"
        binding.tvAvailableProviders.text = "Available Providers: ${data.availableProviders.joinToString(", ")}"
        binding.tvEnabledProviders.text = "Enabled Providers: ${data.enabledProviders.joinToString(", ")}"

        // Update current locations
        if (data.currentLocations.isNotEmpty()) {
            binding.rvCurrentLocations.visibility = View.VISIBLE
            binding.tvNoCurrentData.visibility = View.GONE
            currentLocationsAdapter.submitList(data.currentLocations)
        } else {
            binding.rvCurrentLocations.visibility = View.GONE
            binding.tvNoCurrentData.visibility = View.VISIBLE
        }

        // Update last known locations
        if (data.lastKnownLocations.isNotEmpty()) {
            binding.rvLastKnownLocations.visibility = View.VISIBLE
            binding.tvNoLastKnownData.visibility = View.GONE
            lastKnownLocationsAdapter.submitList(data.lastKnownLocations)
        } else {
            binding.rvLastKnownLocations.visibility = View.GONE
            binding.tvNoLastKnownData.visibility = View.VISIBLE
        }

        // Handle errors
        if (data.errorMessage != null) {
            showError(data.errorMessage)
        } else {
            binding.cardError.visibility = View.GONE
        }
    }

    private fun showError(message: String) {
        binding.cardError.visibility = View.VISIBLE
        binding.tvErrorMessage.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::locationDataManager.isInitialized) {
            locationDataManager.stopLocationUpdates()
        }
        _binding = null
    }
}