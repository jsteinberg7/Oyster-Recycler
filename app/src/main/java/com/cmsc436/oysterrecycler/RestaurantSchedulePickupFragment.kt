package com.cmsc436.oysterrecycler

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cmsc436.oysterrecycler.databinding.RestaurantSchedulePickupFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RestaurantSchedulePickupFragment : Fragment() {
    private lateinit var binding: RestaurantSchedulePickupFragmentBinding

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

                    findNavController().popBackStack(R.id.mainFragment, false)
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

    private fun schedulePickup():Boolean {
        val date = binding.pickupDate.text.toString()
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        val pickupDate = LocalDate.parse(date, formatter)
        val pickUpEpoch = pickupDate.toEpochDay()

        val local = LocalDate.now()
        val localEpoch = local.toEpochDay()
        val checkDate = Regex("^(?=(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)$)")
        if(checkDate.matches(date) &&  (localEpoch >= pickUpEpoch)){
            
            return true
        }else{
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Please enter a valid date!")
            return false
        }
        //TODO: check REGEX
        //TODO: If not date refresh page and ask to submit again
        //TODO: Add Pickup date to Firebase for Potential Drivers

    }
}