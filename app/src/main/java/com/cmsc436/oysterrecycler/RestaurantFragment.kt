package com.cmsc436.oysterrecycler

import Pickup
import Restaurant
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmsc436.oysterrecycler.databinding.RestaurantFragmentBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

class RestaurantFragment : Fragment() {
    private lateinit var binding: RestaurantFragmentBinding
    private lateinit var itemsList: MutableList<String>
    private lateinit var restaurant: Restaurant
    private val firestore = Firebase.firestore
    private var restaurantsCollection = firestore.collection("restaurants")
    private var activePickupsCollection = firestore.collection("activePickups")
    private var completedPickupsCollection = firestore.collection("completedPickups")
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.restaurant_fragment, container, false)
        binding = RestaurantFragmentBinding.inflate(inflater, container, false)
        binding.list.layoutManager = LinearLayoutManager(context)
        displayOrders()
//

        binding.schedulePickup.setOnClickListener {
            findNavController().navigate(R.id.action_restaurantFragment_to_restaurantSchedulePickupFragment)
        }
        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            Toast.makeText(
                requireContext(),
                "You are now logged out!",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().popBackStack(R.id.mainFragment, false)
        }
        return binding.root
    }


    private fun displayOrders() {
        // TODO: Query for active pickup (if exists) + completed pickups for given restaurant
        val restaurantID: String = viewModel.curRestaurantID
        restaurant = Restaurant("", "", "", "", "", listOf(""), listOf(""))
        itemsList = mutableListOf()
        var recentPickups: ArrayList<Pickup> = ArrayList()
        restaurantsCollection
            .document(restaurantID)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    restaurant = Restaurant(
                        UID = document.data?.get("UID").toString(),
                        name = document.data?.get("name").toString(),
                        email = document.data?.get("email").toString(),
                        phone = document.data?.get("phone").toString(),
                        address = document.data?.get("address").toString(),
                        activePickups = document.data?.get("active_pickups") as List<String>,
                        completedPickups = document.data?.get("completed_pickups") as List<String>)


                    for (pickup in restaurant.completedPickups) {
                        completedPickupsCollection
                            .document(pickup)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                                    recentPickups.add(
                                        Pickup(
                                            UID = document.data?.get("UID").toString(),
                                            restaurantID = document.data?.get("restaurantID").toString(),
                                            driverID = document.data?.get("driverID").toString(),
                                            when_date = document.data?.get("when").toString()
                                        )
                                    )
                                } else {
                                    Log.d(ContentValues.TAG, "No such document")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Log.d(ContentValues.TAG, "get failed with ", exception)
                            }
                    }

                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }

        var pickupList: ArrayList<Pickup> = ArrayList()
        // get pickup from pickups collection. Document ID is restaurantID. Add pickup to pickupList
        activePickupsCollection
            .document(restaurantID)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    pickupList.add(
                        Pickup(
                            UID = document.data?.get("UID").toString(),
                            restaurantID = document.data?.get("restaurantID").toString(),
                            driverID = document.data?.get("driverID").toString(),
                            when_date = document.data?.get("when").toString(),
                        )
                    )
                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }

    if(pickupList.size == 0){
        val noAction = "No Active Pickups"
        itemsList.add(noAction)
    }else{
        val action = pickupList[0]
        val str = action.driverID.toString() + " " + action.when_date.toString()
        itemsList.add(str)
    }

    for(i in recentPickups){
        val str = i.driverID.toString() + " " + i.when_date.toString()
        itemsList.add(str)
    }

    binding.list.adapter = RestaurantRecyclerViewAdapter(itemsList, this)
        // 4. Pass list to recycleViewAdapter


    }


}