package com.cmsc436.oysterrecycler

import Driver
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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
    private val viewModel by activityViewModels<MainViewModel>()

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
        val firstName: String = binding.firstName.text.toString()
        val lastName: String = binding.lastName.text.toString()
        val email: String = binding.email.text.toString()
        val password: String = binding.password.text.toString()

        // NOT SURE WHAT TO DO WITH THIS INFO
        val vehicleMake: String = binding.driverVehicleMake.text.toString()
        val vehicleColor: String = binding.driverVehicleColor.text.toString()
        val vehicleLicensePlate: String = binding.driverVehicleLicensePlate.text.toString()

        var emailHash: String = validator.emailHash(email).toString()

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

        if (!validator.validColor(vehicleColor)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.invalid_carColor),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if (!validator.validMake(vehicleMake)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.invalid_carMake),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        if(!validator.validLPlate(vehicleLicensePlate)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.invalid_carPlate),
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
                    // Put in the email hash instead of uid
                    var driver: Driver = Driver(emailHash, firstName = firstName, lastName = lastName,
                        carLicensePlate = vehicleLicensePlate, email = email, carMake = vehicleMake,
                        carColor = vehicleColor, activePickups = listOf(), completedPickups = listOf())

                    dataEngine.createDriverFile(driver = driver)
                    viewModel.curDriverID = emailHash
                    findNavController().navigate(
                        R.id.action_registrationFragment_to_dashboardFragment
                    )
                } else {
                    Toast.makeText(
                        requireContext(),
                        task.exception!!.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}