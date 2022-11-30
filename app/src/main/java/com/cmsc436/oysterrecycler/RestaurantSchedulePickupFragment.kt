package com.example.firebaseemailpasswordexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebaseemailpasswordexample.databinding.RestaurantFragmentBinding
import com.example.firebaseemailpasswordexample.databinding.RestaurantSchedulePickupFragmentBinding
import com.google.firebase.auth.FirebaseAuth

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RestaurantSchedulePickupFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RestaurantSchedulePickupFragment : Fragment() {
    private lateinit var binding: RestaurantSchedulePickupFragmentBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.restaurant_fragment, container, false)
        binding = RestaurantSchedulePickupFragmentBinding.inflate(inflater,container, false)
        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            Toast.makeText(
                requireContext(),
                "You are now logged out!",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().popBackStack(R.id.mainFragment, false)
        }
        schedulePickup()
        return binding.root
    }

    private fun schedulePickup(){
        val date = binding.pickupDate.text.toString()

        //TODO: Add Pickup date to Firebase for Potential Drivers

    }