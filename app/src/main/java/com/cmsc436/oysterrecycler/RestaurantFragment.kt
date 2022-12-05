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
        binding.progressBar.visibility = View.VISIBLE
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

            findNavController().popBackStack(R.id.loginFragment, false)
        }
        return binding.root
    }


    private fun displayOrders() {
        // TODO: Query for active pickup (if exists) + completed pickups for given restaurant
        val restaurantID: String = viewModel.curRestaurantID
        itemsList = mutableListOf()
        var pickupList: ArrayList<Pickup> = ArrayList()


        restaurantsCollection
            .document(restaurantID)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(ContentValues.TAG, "DocumentSnapshot data: ${document.data}")
                    restaurant = Restaurant(
                        document.data?.get("UID").toString(),
                        document.data?.get("name").toString(),
                        document.data?.get("email").toString(),
                        document.data?.get("phone").toString(),
                        document.data?.get("address").toString(),
                        document.data?.get("active_pickups") as List<String>,
                        document.data?.get("completed_pickups") as List<String>)


                    // get pickup from pickups collection. Document ID is restaurantID. Add pickup to pickupList
                    activePickupsCollection
                        .document(restaurantID)
                        .get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                Log.i("test", "active data: ${document.data}")
                                pickupList.add(
                                    Pickup(
                                        UID = document.data?.get("UID").toString(),
                                        restaurantID = document.data?.get("restaurantID").toString(),
                                        driverID = document.data?.get("driverID").toString(),
                                        when_date = document.data?.get("when").toString(),
                                    )
                                )
                                updateDisplay(pickupList)
                                for (pickup in restaurant.completedPickups) {
                                    completedPickupsCollection
                                        .document(pickup)
                                        .get()
                                        .addOnSuccessListener { document ->
                                            if (document != null) {
                                                Log.i("test", "pickup data: ${document.data}")
                                                pickupList.add(
                                                    Pickup(
                                                        UID = document.data?.get("UID").toString(),
                                                        restaurantID = document.data?.get("restaurantID").toString(),
                                                        driverID = document.data?.get("driverID").toString(),
                                                        when_date = document.data?.get("when").toString()
                                                    )
                                                )
                                                updateDisplay(pickupList)
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

                } else {
                    Log.d(ContentValues.TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(ContentValues.TAG, "get failed with ", exception)
            }
        updateDisplay(pickupList)
    }

    private fun updateDisplay(pickupList: ArrayList<Pickup>){
        itemsList = mutableListOf()
        if(pickupList.size == 0){
            val noAction = "No Active Pickups"
            itemsList.add(noAction)
        }else{
            for(i in pickupList){
                val str = i.when_date
                itemsList.add(str)
            }
        }
        binding.progressBar.visibility = View.GONE
        binding.list.adapter = RestaurantRecyclerViewAdapter(itemsList, this)
    }

}