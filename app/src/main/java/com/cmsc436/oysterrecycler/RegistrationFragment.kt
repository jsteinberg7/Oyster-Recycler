package com.cmsc436.oysterrecycler

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.firebaseemailpasswordexample.databinding.RegistrationFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser



class RegistrationFragment : Fragment() {

    private var validator = Validators()
    private lateinit var auth: FirebaseAuth

    /** Binding to XML layout */
    private lateinit var binding: RegistrationFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Use the provided ViewBinding class to inflate the layout.
        binding = RegistrationFragmentBinding.inflate(inflater, container, false)

        auth = requireNotNull(FirebaseAuth.getInstance())

        binding.register.setOnClickListener { registerNewUser() }

        // Return the root view.
        return binding.root
    }

    private fun registerNewUser() {
        val email: String = binding.email.text.toString()
        val password: String = binding.password.text.toString()

        // Going to need to check if the email is already in the system
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