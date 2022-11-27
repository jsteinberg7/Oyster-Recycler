package com.example.firebaseemailpasswordexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseemailpasswordexample.databinding.DashboardFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class DriverFragment : Fragment() {

    // TODO: Query FireStore for nearest 10 locations
    var itemsList = listOf("name 1 \t\t 12 miles", "name 2 \t\t 17 miles", "name 3 \t\t 27 miles", "name 4 \t\t 37 miles")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Use the provided ViewBinding class to inflate
        // the layout and then return the root view.
        val binding = DashboardFragmentBinding.inflate(inflater, container, false)
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = TitlesRecyclerViewAdapter(itemsList, this)

        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            Toast.makeText(
                requireContext(),
                "You are now logged out!",
                Toast.LENGTH_SHORT
            ).show()

            findNavController().popBackStack(R.id.mainFragment, false)
        }

        // Return the root view.
        return binding.root
    }

    fun onItemClick(index: Int): Boolean {
        Toast.makeText(requireContext(), itemsList[index], Toast.LENGTH_SHORT).show()

        // Always allow items to be selected in this app.
        return true
    }
}
