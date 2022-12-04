package com.cmsc436.oysterrecycler

import Restaurant
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cmsc436.oysterrecycler.databinding.FragmentRegistrationRestaurantBinding
import com.cmsc436.oysterrecycler.databinding.RestaurantFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

class RegistrationRestaurantFragment : Fragment() {

    private var validator = Validators()
    private lateinit var auth: FirebaseAuth

    /** Binding to XML layout */
    private lateinit var binding: FragmentRegistrationRestaurantBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Use the provided ViewBinding class to inflate the layout.
        binding = FragmentRegistrationRestaurantBinding.inflate(inflater, container, false)

        auth = requireNotNull(FirebaseAuth.getInstance())

        binding.restaurantRegister.setOnClickListener { registerNewUser() }

        // Return the root view.
        return binding.root
    }

    private fun registerNewUser() {
        val email: String = binding.email.text.toString()
        val password: String = binding.password.text.toString()

        // NOT SURE WHAT TO DO WITH THIS INFO
        val restaurantName: String = binding.restaurantName.text.toString()
        val restaurantAddress: String = binding.restaurantAddress.text.toString()

        val emailHash: String = validator.emailHash(email).toString()


        // Going to need to check if the email is already in the system
        // Also need to include checks for color, make, or plate being invalid
        if (!validator.validEmail(email)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.invalid_email),
                Toast.LENGTH_LONG
            ).show()

            return
        }

        if (!validator.validPassword(password)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.invalid_password),
                Toast.LENGTH_LONG
            ).show()

            return
        }

        binding.progressBar.visibility = View.VISIBLE

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {

                    val firebaseUser: FirebaseUser = task.result!!.user!!

                    Toast.makeText(
                        requireContext(),
                        getString(R.string.register_success_string),
                        Toast.LENGTH_LONG
                    ).show()

                    // Whatever you pass in doesn't matter
                    var dataEngine = DataEngine(firebaseUser.uid)
                    // Replaced firebaseUser.uid with emailHash
                    var restaurant: Restaurant = Restaurant(emailHash, name = restaurantName, email = "last", phone = "123456",
                        address = restaurantAddress, activePickups = listOf(), completedPickups = listOf())

                    dataEngine.createRestaurantFile(restaurant = restaurant)

                    findNavController().navigate(
                        R.id.action_registrationFragment_to_dashboardFragment
                    )

                } else {
                    Toast.makeText(
                        requireContext(),
                        // Could add this instead
                        //task.exception!!.message.toString(),
                        getString(R.string.register_failed_string),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}