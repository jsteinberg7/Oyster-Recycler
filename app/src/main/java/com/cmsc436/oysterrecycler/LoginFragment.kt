package com.cmsc436.oysterrecycler

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.cmsc436.oysterrecycler.databinding.LoginFragmentBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private var validator = Validators()
    /** Binding to XML layout */
    private lateinit var binding: LoginFragmentBinding
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Use the provided ViewBinding class to inflate the layout.
        binding = LoginFragmentBinding.inflate(inflater, container, false)

        firebaseAuth = requireNotNull(FirebaseAuth.getInstance())

        binding.login.setOnClickListener {
            //loginUserAccount()
            findNavController().navigate(R.id.action_loginFragment_to_permission_fragment)
        }
        binding.registerDriver.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_driverRegistration)
        }
        binding.registerRestaurant.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_restaurantRegistration)
        }

        // Return the root view.
        return binding.root
    }


    private fun loginUserAccount() {
        val email: String = binding.email.text.toString()
        val password: String = binding.password.text.toString()

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.login_toast),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(
                requireContext(),
                getString(R.string.password_toast),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                    task ->
                binding.progressBar.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "Login successful!",
                        Toast.LENGTH_LONG
                    ).show()
                    // Can pass in whatever to data engine
                    var dataEngine = DataEngine("")
                    val emailHash: String = validator.emailHash(email).toString()

                    dataEngine.driversCollection.document(emailHash).get().addOnSuccessListener {
                        document ->

                        if (document.data.isNullOrEmpty()) {
                            // This will auto take them to to the restaurant fragment bc they're not a driver
                            // and all users are either a restaurant or a driver
                            viewModel.curRestaurantID = emailHash
                            findNavController().navigate(R.id.action_loginFragment_to_restaurant_fragment)
                        } else {
                            // User is a driver
                            viewModel.curDriverID = emailHash
                            findNavController().navigate(R.id.action_loginFragment_to_permission_fragment)
                        }
                    }


                    //TODO: query for driverID/restaurantID based on login info then navigate to appropriate fragment
//                    viewModel.curDriverID = 10
//                    viewModel.curRestaurantID = 20
//                    findNavController().navigate(R.id.action_loginFragment_to_permission_fragment)
//                    findNavController().navigate(R.id.action_loginFragment_to_restaurant_fragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Login failed! Please try again later",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}
