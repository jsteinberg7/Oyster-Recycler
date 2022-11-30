package com.cmsc436.oysterrecycler

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebaseemailpasswordexample.databinding.RestaurantFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class RestaurantFragment : Fragment() {
    private lateinit var binding: RestaurantFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        inflater.inflate(R.layout.restaurant_fragment, container, false)
        binding = RestaurantFragmentBinding.inflate(inflater, container, false)
        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            Toast.makeText(
                requireContext(),
                "You are now logged out!",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().popBackStack(R.id.mainFragment, false)
        }
        displayDrivers()
        return binding.root
    }


    private fun displayDrivers(){
        //TODO: Query for Drivers under Restaurant name
        //TODO: Display Drivers who have completed Orders in the past
        //TODO: List them in order of most recent pickup
    }


}