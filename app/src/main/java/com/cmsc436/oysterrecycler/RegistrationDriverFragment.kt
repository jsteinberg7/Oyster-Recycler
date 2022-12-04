package com.cmsc436.oysterrecycler

import Driver
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.cmsc436.oysterrecycler.databinding.FragmentRegistrationDriverBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

class RegistrationDriverFragment : Fragment() {

    private var validator = Validators()
    private lateinit var auth: FirebaseAuth

    /** Binding to XML layout */
    private lateinit var binding: FragmentRegistrationDriverBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Use the provided ViewBinding class to inflate the layout.
        //binding = RegistrationFragmentBinding.inflate(inflater, container, false)
        binding = FragmentRegistrationDriverBinding.inflate(inflater, container, false)


        auth = requireNotNull(FirebaseAuth.getInstance())

        binding.driverRegister.setOnClickListener { registerNewUser() }

        // Return the root view.
        return binding.root
    }

    private fun registerNewUser() {
        val email: String = binding.email.text.toString()
        val password: String = binding.password.text.toString()

        // NOT SURE WHAT TO DO WITH THIS INFO
        val vehicleMake: String = binding.driverVehicleMake.text.toString()
        val vehicleColor: String = binding.driverVehicleColor.text.toString()
        val vehicleLicensePlate: String = binding.driverVehicleLicensePlate.text.toString()


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

                    var dataEngine = DataEngine(firebaseUser.uid)
                    var driver: Driver = Driver(firebaseUser.uid, firstName = "first", lastName = "last", phone = "123456",
                        email = email, carMake = vehicleMake, carModel = "test Model", activePickups = listOf(), completedPickups = listOf())

                    dataEngine.createDriverFile(driver = driver)

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