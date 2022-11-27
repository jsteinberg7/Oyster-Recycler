package com.example.firebaseemailpasswordexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebaseemailpasswordexample.databinding.MainFragmentBinding

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Use the provided ViewBinding class to inflate the layout.
        val binding = MainFragmentBinding.inflate(inflater, container, false)

        binding.login.setOnClickListener {
            findNavController().navigate(
                R.id.action_mainFragment_to_driver
            )
        }

        binding.register.setOnClickListener {
            findNavController().navigate(
                R.id.action_mainFragment_to_registrationFragment
            )
        }

        // Return the root view.
        return binding.root
    }
}