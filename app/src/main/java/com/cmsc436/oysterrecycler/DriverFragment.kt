package com.cmsc436.oysterrecycler

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmsc436.oysterrecycler.databinding.DriverFragmentBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import org.xmlpull.v1.sax2.Driver

class DriverFragment : Fragment() {

    private val viewModel by activityViewModels<MainViewModel>()
    // TODO: Query FireStore for driver's pickups and their adresses
    var itemsList = listOf("Nick", "UMD", "VT", "Hassam")
    var addressList = listOf("18311 Leedstown Way", "3972 Campus Drive", "260 Alumnai Mall", "4519 Winding Oak Drive")
    var idx = -1
    private lateinit var binding: DriverFragmentBinding
    private lateinit var driver: Driver

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Use the provided ViewBinding class to inflate
        // the layout and then return the root view.
        val driverId = viewModel.curDriverID
        binding = DriverFragmentBinding.inflate(inflater, container, false)
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = DriverRecyclerViewAdapter(itemsList, this)

        Log.i("test", driverId.toString())

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
            // TODO: Query FireStore to take job off driver and then query to update lists
            itemsList = itemsList.filter{itemsList.indexOf(it) != idx}
            binding.list.adapter = DriverRecyclerViewAdapter(itemsList, this)
        }

        binding.finish.setOnClickListener {
            if (idx >= 0) {
                // TODO: query to get rid of job on restaurant and driver and query to update list
                Toast.makeText(requireContext(), itemsList[idx] + " finished", Toast.LENGTH_SHORT).show()
                itemsList = itemsList.filter{itemsList.indexOf(it) != idx}
                binding.list.adapter = DriverRecyclerViewAdapter(itemsList, this)
            }
        }

        binding.directions.setOnClickListener {
            try {
                // Process text for network transmission
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
                // Log any error messages to LogCat using Log.e()
                Log.e("Error", e.toString())
            }
        }

        binding.findPickup.setOnClickListener {
            findNavController().navigate(
                R.id.action_driverFragment_to_driverFindJobFragment
            )
        }
        // Return the root view.
        return binding.root
    }

    fun onItemClick(index: Int): Boolean {
        idx = index
        binding.finish.visibility = 1
        binding.cancel.visibility = 1
        binding.directions.visibility = 1
        (binding.list.adapter as DriverRecyclerViewAdapter).select(index)
        // Always allow items to be selected in this app.
        return true
    }


}
