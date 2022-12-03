package com.cmsc436.oysterrecycler

import android.app.AlertDialog
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmsc436.oysterrecycler.databinding.DriverFindJobFragmentBinding
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import java.time.Duration


class DriverFindJobFragment : Fragment() {
    private val viewModel by activityViewModels<MainViewModel>()
    lateinit var itemsList: List<String>
    var idx = -1
    private lateinit var binding: DriverFindJobFragmentBinding
    private var location: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var firstUpdate = true
    lateinit var address: Address

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.create().apply {
                interval = Duration.ofSeconds(0).toMillis()
                fastestInterval = Duration.ofSeconds(0).toMillis()
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            },
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Use the provided ViewBinding class to inflate
        // the layout and then return the root view.
        binding = DriverFindJobFragmentBinding.inflate(inflater, container, false)
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.logout.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to Logout?")
                .setCancelable(true)
                .setPositiveButton("Yes") { _, _ ->
                    FirebaseAuth.getInstance().signOut()

                    Toast.makeText(
                        requireContext(),
                        "You are now logged out!",
                        Toast.LENGTH_SHORT
                    ).show()

                    findNavController().popBackStack(R.id.mainFragment, false)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        binding.refresh.setOnClickListener {
            // TODO: Query FireStore for nearest 10-15 locations
            itemsList = listOf("name 5 \t-\t 12 miles away", "name 6 \t-\t 17 miles away", "name 7 \t-\t 27 miles away", "name 8 \t-\t 37 miles away")
            updateAdapter()
        }

        binding.accept.setOnClickListener {
            if (idx >= 0) {
                findNavController().navigate(
                    R.id.action_driverFindJobFragment_to_driverFragment
                )
                // TODO: query to add this pickup to this driver using driverID
                Toast.makeText(requireContext(), itemsList[idx] + " added", Toast.LENGTH_SHORT).show()
            }
        }

        binding.currentPickups.setOnClickListener {
            findNavController().navigate(
                R.id.action_driverFindJobFragment_to_driverFragment
            )
        }

        // Return the root view.
        return binding.root
    }

    fun onItemClick(index: Int): Boolean {
        idx = index
        binding.accept.visibility = 1
        (binding.list.adapter as DriverFindRecyclerViewAdapter).select(index)
        // Always allow items to be selected in this app.
        return true
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            location = locationResult.lastLocation
            if (firstUpdate) {
                Log.i("test", "Long: " + location?.longitude.toString() + " Lat: " + location?.latitude.toString())
                val driverId = viewModel.curDriverID
                // TODO: Query FireStore for nearest 10-15 locations into itemsList using location
                itemsList = listOf("name 1 \t-\t 12 miles away", "name 2 \t-\t 17 miles away", "name 3 \t-\t 27 miles away", "name 4 \t-\t 37 miles away")
                updateAdapter()
                firstUpdate = false
                val coder = Geocoder(context)
                address = coder.getFromLocationName("18311 Leedstown Way", 1)[0]
                Log.i("test", "Address Long: " + address.longitude.toString() + " Address Lat: " + address.latitude.toString())
            }
        }
    }

    private fun updateAdapter() {
        binding.list.adapter = DriverFindRecyclerViewAdapter(itemsList, this)
    }
}
