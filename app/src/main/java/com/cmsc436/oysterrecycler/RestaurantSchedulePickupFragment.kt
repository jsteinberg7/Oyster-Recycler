package com.cmsc436.oysterrecycler

import Pickup
import Restaurant
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cmsc436.oysterrecycler.databinding.RestaurantSchedulePickupFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*



class RestaurantSchedulePickupFragment : Fragment() {
    private val firestore = Firebase.firestore
    private var restaurantsCollection = firestore.collection("restaurants")
    private lateinit var binding: RestaurantSchedulePickupFragmentBinding
    private val viewModel by activityViewModels<MainViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.restaurant_fragment, container, false)
        binding = RestaurantSchedulePickupFragmentBinding.inflate(inflater, container, false)
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
        binding.submitPickupRequest.setOnClickListener {
            if(schedulePickup()){
                findNavController().navigate(R.id.action_restaurantSchedulePickupFragment_to_restaurantFragment)
            }
        }

        return binding.root
    }

    private fun schedulePickup(): Boolean {

        //TODO: check REGEX
        //TODO: If not date refresh page and ask to submit again
        //TODO: Add Pickup date to Firebase for Potential Drivers

        val local = LocalDate.now()

        // + 1 because datePicker indexes months [0-11]
        val month = binding.datePicker.month + 1
        val day = binding.datePicker.dayOfMonth
        val year = binding.datePicker.year




        // 1. Get the date using datePicker
//        val datePicker = binding.datePicker
//        val day = datePicker.dayOfMonth
//        val month = datePicker.month
//        val year = datePicker.year
        // show snack bar with the date
        Toast.makeText(
            requireContext(),
            "Date: $month/$day/$year",
            Toast.LENGTH_LONG
        ).show()
        
        // 2. Check if the date is valid
        // 3. Check if the date is in the future
        // 4. Check if restaurant already has active pickup
        // 5. Create pickup
        // 6. Add pickup to firebase
        var day2 = if (day < 9) { "0" + day.toString() } else { day.toString() }
        val pickup = Pickup(UID = viewModel.curRestaurantID,
              restaurantID = viewModel.curRestaurantID, driverID = "", when_date = "$month/$day2/$year")
        val dataEngine = DataEngine()
//        dataEngine.addActivePickupToRestaurant(restaurant = )
        return if ((year >= local.year) && (month >= local.monthValue) && (day >= local.dayOfMonth)) {
            dataEngine.createPickupFile(pickup)
            restaurantsCollection.document(viewModel.curRestaurantID).get().addOnSuccessListener { document ->
                var restaurant = Restaurant(UID = document.data?.get("UID").toString(),
                    name = document.data?.get("name").toString(),
                    email = document.data?.get("email").toString(),
                    phone = document.data?.get("phone").toString(),
                    address = document.data?.get("address").toString(),
                    activePickups = document.data?.get("active_pickups") as List<String>,
                    completedPickups = document.data?.get("completed_pickups") as List<String>)
                if (restaurant.activePickups.size == 0) {
                    restaurant.activePickups += viewModel.curRestaurantID
                    restaurantsCollection.document(viewModel.curRestaurantID).set(restaurant.serialize())
                }
            }
            true
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.incorrect_date),
                Toast.LENGTH_LONG
            ).show()
            false
        }
    }
}