package com.cmsc436.oysterrecycler

import Pickup
import Restaurant
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmsc436.oysterrecycler.databinding.RestaurantFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class RestaurantFragment : Fragment() {
    private lateinit var binding: RestaurantFragmentBinding
    private lateinit var itemsList: List<String>
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.restaurant_fragment, container, false)
        binding = RestaurantFragmentBinding.inflate(inflater, container, false)
        binding.list.layoutManager = LinearLayoutManager(context)
//        binding.list.adapter = RestaurantRecyclerViewAdapter(itemsList, this)

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
        displayOrders()
        return binding.root
    }


    private fun displayOrders() {
        // TODO: Query for active pickup (if exists) + completed pickups for given restaurant
        val dataEngine: DataEngine = DataEngine()
        val restaurantID: String = viewModel.curRestaurantID
        // 1. Get active
        val activePickups: ArrayList<Pickup> = dataEngine.getActivePickup(restaurantID = restaurantID)
        // 2. Get complete
        val restaurant: Restaurant = dataEngine.getRestaurantByUID(restaurantID)
        val completedPickups: ArrayList<Pickup> = dataEngine.getRecentCompletedPickups(restaurant = restaurant, num_pickups = 10)
        // 3. Merge list

        // 4. Pass list to recycleViewAdapter

    }


}