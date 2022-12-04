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
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.Duration
import kotlin.math.*

class DriverFindJobFragment : Fragment() {
    private val firestore = Firebase.firestore
    private var driversCollection = firestore.collection("drivers")
    private var restaurantsCollection = firestore.collection("restaurants")
    private var pickupsCollection = firestore.collection("activePickups")
    private val viewModel by activityViewModels<MainViewModel>()
    lateinit var itemsList: MutableList<String>
    lateinit var distList: MutableList<Double>
    lateinit var addressList: MutableList<String>
    lateinit var nameList: MutableList<String>
    var idx = -1
    private lateinit var binding: DriverFindJobFragmentBinding
    private var location: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var firstUpdate = true
    lateinit var address: Address
    private lateinit var driverId: String

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
        driverId = viewModel.curDriverID
        binding = DriverFindJobFragmentBinding.inflate(inflater, container, false)
        binding.list.layoutManager = LinearLayoutManager(context)
        itemsList = mutableListOf()
        distList = MutableList(10) {(-1).toDouble()}
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
            getLocations()
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
                Log.i("test", "Coords: " + location?.latitude.toString() + " " + location?.longitude.toString())
                val driverId = viewModel.curDriverID
                // TODO: Query FireStore for nearest 10-15 locations into itemsList using location
                getLocations()
                //itemsList = mutableListOf("name 1 \t-\t 12 miles away", "name 2 \t-\t 17 miles away", "name 3 \t-\t 27 miles away", "name 4 \t-\t 37 miles away")
                //updateAdapter()
//                val coder = Geocoder(context)
//                try {
//                    address = coder.getFromLocationName("18311 Leedstown Way", 1)[0]
//                }
//                catch (e: Error) {
//                    Log.i("test", e.toString())
//                }
//                Log.i("test", "Address Coords: " + address.latitude.toString() + " " + address.longitude.toString())
//                Log.i("test", "Distance: " + distance(address.latitude, address.longitude, location!!.latitude, location!!.longitude).toString() )
                firstUpdate = false
            }
        }
    }

    // formula from https://stackoverflow.com/questions/6981916/how-to-calculate-distance-between-two-locations-using-their-longitude-and-latitu
    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    private fun updateAdapter() {
        val coder = Geocoder(context)
        var closestLimit = (-1).toDouble()
        itemsList = mutableListOf()
        distList = mutableListOf()
        for (addr in addressList) {
            var loc = coder.getFromLocationName(addr, 1)[0]
            var dist = distance(loc.latitude, loc.longitude, location!!.latitude, location!!.longitude)
            if (closestLimit == (-1).toDouble()) {
                itemsList += nameList[addressList.indexOf(addr)] + " - " + dist.toInt().toString() + " miles away"
                distList += dist
                var i = itemsList.size - 1
                while (i > 0 && distList[i - 1] > dist) {
                    var temp = itemsList[i-1]
                    var temp2 = distList[i-1]
                    itemsList[i-1] = itemsList[i]
                    distList[i-1] = distList[i]
                    itemsList[i] = temp
                    distList[i] = temp2
                    i -= 1
                }
                if (itemsList.size == 10) {
                    closestLimit = distList[9]
                }
            }
            else if (dist < closestLimit) {
                itemsList[9] = nameList[addressList.indexOf(addr)] + " - " + dist.toInt().toString() + " miles away"
                var i = 9
                while (i > 0 && distList[i - 1] > dist) {
                    var temp = itemsList[i-1]
                    var temp2 = distList[i-1]
                    itemsList[i-1] = itemsList[i]
                    distList[i-1] = distList[i]
                    itemsList[i] = temp
                    distList[i] = temp2
                    i -= 1
                }
            }

//            if (dist < closestLimit || closestLimit == (-1).toDouble()) {
//                if (itemsList.size < 10) {
//                    itemsList += nameList[addressList.indexOf(addr)] + " - " + dist.toString() + " miles away"
//                }
//                else {
//                    itemsList[9] = nameList[addressList.indexOf(addr)] + " - " + dist.toString() + " miles away"
//                    var i = 9
//                    while (i > 0 && (distList[i] > dist || distList[i] == (-1).toDouble())) {
//                        var temp = itemsList[i-1]
//                        var temp2 = distList[i-1]
//                        itemsList[i-1] = itemsList[i]
//                        distList[i-1] = distList[i]
//                        itemsList[i] = temp
//                        distList[i] = temp2
//                        i = i - 1
//                    }
//                }
//            }
        }
        Log.i("test", "Update: " + itemsList.toString())
        binding.list.adapter = DriverFindRecyclerViewAdapter(itemsList, this)
    }

    private fun getLocations() {
        pickupsCollection.get().addOnSuccessListener { documents ->
            addressList = mutableListOf()
            nameList = MutableList(documents.size()) {""}
            for (document in documents) {
                Log.i("test","got: " + document.data.toString())
                if (document.data != null && document.data.get("driver_id") == null) {
                    Log.i("test", "using" + document.data.toString())
                    var restaurant = document.data.get("restaurant_id").toString()
                    restaurantsCollection.document(restaurant).get().addOnSuccessListener { doc ->
                        if (doc.data != null) {
                            addressList += doc.data?.get("address").toString()
                            nameList[addressList.indexOf(doc.data?.get("address").toString())] = doc.data?.get("name").toString()
                            updateAdapter()
                        }
                    }
                }
            }
        }
    }
}
