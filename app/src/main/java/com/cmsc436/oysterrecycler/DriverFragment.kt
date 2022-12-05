package com.cmsc436.oysterrecycler

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
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
import com.cmsc436.oysterrecycler.databinding.DriverFragmentBinding
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import Driver
import Restaurant
import android.content.ContentValues
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Document
import java.time.Duration
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class DriverFragment : Fragment() {

    private val firestore = Firebase.firestore
    private var driversCollection = firestore.collection("drivers")
    private var restaurantsCollection = firestore.collection("restaurants")
    private var pickupsCollection = firestore.collection("activePickups")
    private var finishedCollection = firestore.collection("completedPickups")
    private val viewModel by activityViewModels<MainViewModel>()
    private lateinit var driverId: String
    lateinit var itemsList: MutableList<String>
    lateinit var assignments: MutableList<String>
    lateinit var addressList: MutableList<String>
    lateinit var dateList: MutableList<String>
    var idx = -1
    private lateinit var binding: DriverFragmentBinding
    private lateinit var driver: Driver
    private lateinit var dataEngine: DataEngine
    private val dropoffs = listOf("723 Second Street, Annapolis, MD 21401",
        "512 Severn Avenue, Annapolis, MD 21403", "317 First Street, Annapolis, MD 21403",
        "1805 Virginia Street, Annapolis, MD 21401", "6 Herndon Avenue, Annapolis, MD 21403",
        "5400 Nutwell Sudley Road, Deale, MD 20751", "Scriveners Road, Edgewater, MD 21037",
        "2820 Solomons Island Road, Edgewater, MD 21037",
        "7220 Grayburn Drive, Glen Burnie MD 21061", "100 Dover Road, Glen Burnie, MD 21060",
        "389 Burns Crossing Road, Severn, MD  21144", "503 Ritchie Highway, Severna Park, MD 21146",
        "4800 Atwell Road, Shady Side, MD 20764", "2840 Sisson Street, Baltimore MD 21211",
        "2501 Putty Hill Avenue, Parkville MD 21214", "11930 Holly Road, Ridgely, MD 21660",
        "16643 Melville Road, Henderson, MD 21640", "4230 Old Denton Road, Federalsburg, MD 21632",
        "105 Back Landing Road, Preston, MD 21655",
        "1400 Baltimore Boulevard, Westminster, MD 21157",
        "1043 Liberty Road, Eldersburg, MD 21784", "1043 Liberty Road, Eldersburg, MD 21784",
        "401 Sweetwater Road, Lusby, MD 20657", "1045 Ball Road, St. Leonard, MD 20685",
        "350 Stafford Road, Barstow, MD 20610",
        "5230 Breezy Point Road, Chesapeake Beach, MD 20732",
        "3666 Hunting Creek Road, Huntingtown, MD 20639",
        "11725 H. G. Trueman Road, Lusby, MD 20657",
        "96 Pushaw Station Road, Sunderland, MD 20689",
        "2801 Plum Point Road, Huntingtown, MD 20639",
        "15950 Cobb Island Road, Cobb Island, MD 20625",
        "12305 Billingsley Road, Waldorf, MD 20602",
        "13140 Charles Street, Charlotte Hall, MD 20646",
        "6645 Mason Springs Road, Pisgah, MD 20640",
        "6815 East New Market Ellwood Road, Hurlock, MD 21643",
        "3270 Golden Hill Road, Church Creek, MD 21622",
        "5915 Cambridge Road, Route 14, Secretary, MD 21664",
        "2020 Horns Point Road, Cambridge, MD 21613",
        "4607 Wedgewood Boulevard, Frederick MD 21703",
        "5640 Urbana Pike, Frederick, MD  21704",
        "6692 Cedar Lane, Columbia, MD 21044",
        "927 Pulaski Hwy, U.S. Rt. 40",
        "3241 Scarboro Road, Street, MD 21154",
        "104 South Philadelphia Blvd.",
        "471 S Cross Street, Chestertown, MD 21620",
        "6007 N Main Street, Rock Hall, MD 21661",
        "401 Gravel Run Road, Grasonville, MD 21638",
        "433 Kent Narrows Way North, Grasonville, MD 21638",
        "37766 New Market Turner Road, Charlotte Hall, MD 20622",
        "24547 Horseshoe Road, Clements, MD 20624",
        "26630 North Sandgates Road, Mechanicsville, MD 20659",
        "Tall Timbers Road, Tall Timbers, MD 20690",
        "44825 Old St. Andrews Church Road, California, MD 20619",
        "45350 Happyland Road, Valley Lee, MD 20692",
        "13939 Point Lookout Road, Ridge, MD 20680",
        "29012 Mt. Vernon Road, Princess Anne, MD 21853",
        "24019 Deal Island Road, Deal Island, MD 21821",
        "8716 James Ring Road, Westover, MD 21871",
        "4941 Crisfield Highway, Crisfield, MD 21817",
        "8405 Wallace Taylor Road, Pocomoke, MD 21851",
        "114 S Washington Street, Suite 103, Easton, MD 21601",
        "213 North Talbot Street, St. Michaels MD 21663",
        "7341 Barkers Landing Road, Easton, MD 21601",
        "31966 Fooks Road, Salisbury, MD 21804",
        "20906 Nanticoke Road, Bivalve, MD 21814",
        "27774 Waller Road, Salisbury, MD 21801",
        "27778 Walnut Tree Road, Salisbury, MD 21801",
        "8301 Old Railroad Rd, Hebron, MD 21803",
        "9290 Athol Road, Mardela Spring, MD 21837",
        "6969 Brick Kiln Road, Salisbury, MD 21801",
        "6928 Eastside Road, Parsonsburg, MD 21849",
        "34333 Mt. Hermon Road, Parsonsburg, MD 21849",
        "25202 Nanticoke Road, Quantico, MD 21856",
        "12287 Twilford Road, Sharptown, MD 21861",
        "117 Washington Avenue, Colonial Beach, VA 22443")

    private var location: Location? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.create().apply {
                interval = Duration.ofSeconds(1).toMillis()
                fastestInterval = Duration.ofSeconds(1).toMillis()
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

        // Query FireStore for driver's pickups and their adresses based on driver id
        getDriverAssignments()
        itemsList = mutableListOf()
        assignments = mutableListOf()
        binding = DriverFragmentBinding.inflate(inflater, container, false)
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = DriverRecyclerViewAdapter(itemsList, this)

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

                    findNavController().popBackStack(R.id.loginFragment, false)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

        binding.cancel.setOnClickListener {
            if (idx >= 0 && binding.toggle.isChecked) {
                pickupsCollection.document(assignments[idx]).get().addOnSuccessListener { document ->
                    var map = HashMap<String, Any>()
                    map["restaurant_id"] = document.data?.get("restaurant_id").toString()
                    map["driver_id"] = ""
                    map["when"] = document.data?.get("when").toString()
                    pickupsCollection.document(assignments[idx]).set(map)
                    driver.activePickups = driver.activePickups.filter { it != assignments[idx] }
                    driversCollection.document(driverId).set(driver.serialize())
                    getDriverAssignments()
                }
                itemsList = itemsList.filter{itemsList.indexOf(it) != idx} as MutableList<String>
                binding.list.adapter = DriverRecyclerViewAdapter(itemsList, this)
            }
        }

        binding.finish.setOnClickListener {
            if (idx >= 0 && binding.toggle.isChecked) {
                Log.i("test", "assignment:" + assignments[idx])
                pickupsCollection.document(assignments[idx]).get().addOnSuccessListener { document ->
                    var dateAssigned = document.data?.get("when").toString()
                    var id = assignments[idx] + driverId + dateAssigned.replace('/', 'l')
                    pickupsCollection.document("5").delete()
                    driver.activePickups = driver.activePickups.filter { it != assignments[idx] }
                    driver.completedPickups += id
                    driversCollection.document(driverId).set(driver.serialize())
                    restaurantsCollection.document(assignments[idx]).get().addOnSuccessListener { document ->
                        var restaurant = Restaurant(UID = document.data?.get("UID").toString(),
                            name = document.data?.get("name").toString(),
                            email = document.data?.get("email").toString(),
                            phone = document.data?.get("phone").toString(),
                            address = document.data?.get("address").toString(),
                            activePickups = document.data?.get("active_pickups") as List<String>,
                            completedPickups = document.data?.get("completed_pickups") as List<String>)
                        restaurant.activePickups = listOf()
                        restaurantsCollection.document(restaurant.UID).set(restaurant.serialize())
                        getDriverAssignments()
                    }
                    var map = HashMap<String, Any>()
                    map["UID"] = id
                    map["driver_id"] = driverId
                    map["restaurant_id"] = assignments[idx]
                    map["when"] = dateAssigned
                    finishedCollection.document(id).set(map)
                }
                Toast.makeText(requireContext(), itemsList[idx] + " finished", Toast.LENGTH_SHORT).show()
                itemsList = itemsList.filter{itemsList.indexOf(it) != idx} as MutableList<String>
                binding.list.adapter = DriverRecyclerViewAdapter(itemsList, this)
            }
        }

        binding.directions.setOnClickListener {
            try {
                val address = addressList[idx].replace(' ', '+')

                val geoIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=$address")
                )
                activity?.let { it1 ->
                    geoIntent.resolveActivity(it1.packageManager)?.let {
                        startActivity(geoIntent)
                    }
                }

            } catch (e: Exception) {
                Log.e("Error", e.toString())
            }
        }

        binding.findPickup.setOnClickListener {
            findNavController().navigate(
                R.id.action_driverFragment_to_driverFindJobFragment
            )
        }

        binding.dropoff.setOnClickListener {
            findClosestAddress()
        }

        binding.refresh.setOnClickListener {
            getDriverAssignments()
        }
        binding.toggle.setOnClickListener {
            if (binding.toggle.isChecked) {
                getDriverAssignments()
            }
            else {
                getDriverHistory()
            }
            binding.finish.visibility = 0
            binding.cancel.visibility = 0
            binding.directions.visibility = 0
            idx = -1
        }
        // Return the root view.
        return binding.root
    }

    fun onItemClick(index: Int): Boolean {
        idx = index
        if (binding.toggle.isChecked) {
            binding.finish.visibility = 1
            binding.cancel.visibility = 1
        }
        binding.directions.visibility = 1
        (binding.list.adapter as DriverRecyclerViewAdapter).select(index)
        // Always allow items to be selected in this app.
        return true
    }

    fun findClosestAddress() {
        if (location != null) {
            var closest = (-1).toDouble()
            var closestAddress = dropoffs[0]
            val coder = Geocoder(context)
            for (element in dropoffs) {
                var address = coder.getFromLocationName(element, 1)[0]
                var dist = distance(address.latitude, address.longitude, location!!.latitude, location!!.longitude)
                if (closest == (-1).toDouble() || dist < closest) {
                    closest = dist
                    closestAddress = element
                }
            }
            try {
                // Process text for network transmission
                val address = closestAddress.replace(", ", "+").replace(' ', '+')

                val geoIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("geo:0,0?q=$address")
                )
                activity?.let { it1 ->
                    geoIntent.resolveActivity(it1.packageManager)?.let {
                        startActivity(geoIntent)
                    }
                }

            } catch (e: Exception) {
                // Log any error messages to LogCat using Log.e()
                Log.e("Error", e.toString())
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

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            location = locationResult.lastLocation
        }
    }

    private fun getDriverAssignments() {
        driverId = viewModel.curDriverID
        driversCollection
            .document(driverId)
            .get()
            .addOnSuccessListener { document ->
                Log.i("test", document.toString())
                if (document.data != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    driver = Driver(
                        document.data?.get("UID").toString(),
                        document.data?.get("firstname").toString(),
                        document.data?.get("lastname").toString(),
                        document.data?.get("email").toString(),
                        document.data?.get("phone").toString(),
                        document.data?.get("car_make").toString(),
                        document.data?.get("car_model").toString(),
                        document.data?.get("active_pickups") as List<String>,
                        document.data?.get("completed_pickups") as List<String>

                    )
                    var pickups = driver.activePickups
                    itemsList = MutableList(pickups.size) { "" }
                    addressList = MutableList(pickups.size) { "" }
                    assignments = MutableList(pickups.size) { "" }
                    Log.i("test", pickups.toString())
                    binding.info.text = "Welcome " + driver.email + "!"
                    for (pickup in pickups) {
                        restaurantsCollection.document(pickup).get()
                            .addOnSuccessListener { restaurant ->
                                if (restaurant.data != null) {
                                    itemsList[pickups.indexOf(pickup)] =
                                        restaurant.data?.get("name").toString()
                                    assignments[pickups.indexOf(pickup)] =
                                        restaurant.data?.get("UID").toString()
                                    addressList[pickups.indexOf(pickup)] =
                                        restaurant.data?.get("address").toString()
                                    binding.list.adapter = DriverRecyclerViewAdapter(itemsList, this)
                                } else {
                                    Log.d(ContentValues.TAG, "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d(ContentValues.TAG, "get failed with ", exception)
                            }
                    }
                    Log.i("test", "updating View")
                } else {
                    Log.i("test", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.i("test", "get failed with ", exception)
            }
    }

    private fun getDriverHistory() {
        driverId = viewModel.curDriverID
        driversCollection
            .document(driverId)
            .get()
            .addOnSuccessListener { document ->
                Log.i("test", document.toString())
                if (document.data != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    driver = Driver(
                        document.data?.get("UID").toString(),
                        document.data?.get("firstname").toString(),
                        document.data?.get("lastname").toString(),
                        document.data?.get("email").toString(),
                        document.data?.get("phone").toString(),
                        document.data?.get("car_make").toString(),
                        document.data?.get("car_model").toString(),
                        document.data?.get("active_pickups") as List<String>,
                        document.data?.get("completed_pickups") as List<String>

                    )
                    var pickups = driver.completedPickups
                    itemsList = MutableList(pickups.size) { "" }
                    addressList = MutableList(pickups.size) { "" }
                    Log.i("test", pickups.toString())
                    for (pickup in pickups) {
                        finishedCollection.document(pickup).get()
                            .addOnSuccessListener { completed ->
                                if (completed.data != null) {
                                    var resid = completed.data?.get("restaurant_id").toString()
                                    restaurantsCollection.document(resid).get().addOnSuccessListener { restaurant ->
                                        itemsList[pickups.indexOf(pickup)] =
                                            "Pick up from " + restaurant.data?.get("name").toString() + " on " + completed.data?.get("when").toString()
                                        addressList[pickups.indexOf(pickup)] = restaurant.data?.get("address")
                                            .toString()
                                        binding.list.adapter =
                                            DriverRecyclerViewAdapter(itemsList, this)
                                    }
                                } else {
                                    Log.d(ContentValues.TAG, "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d(ContentValues.TAG, "get failed with ", exception)
                            }
                    }
                    binding.list.adapter = DriverRecyclerViewAdapter(itemsList, this)
                    Log.i("test", "updating View: $itemsList")
                } else {
                    Log.i("test", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.i("test", "get failed with ", exception)
            }
    }

}
