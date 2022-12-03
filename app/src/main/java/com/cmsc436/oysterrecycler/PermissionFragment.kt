package com.cmsc436.oysterrecycler

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class PermissionFragment : Fragment() {
    private val PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FrameLayout(requireContext()).also {
            it.id = View.generateViewId()
        }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                // All permission are granted - navigate to next fragment
                findNavController().navigate(R.id.action_permission_fragment_to_driver_fragment)
            } else {
                // Permission denied - inform user that permission is needed.
                Snackbar.make(requireView(), "This app requires permission to access your location.", Snackbar.LENGTH_INDEFINITE).apply {
                    setAction("OK") {
                        relaunchPermissionRequest()
                    }
                }
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val granted =
            ContextCompat.checkSelfPermission(requireContext(), PERMISSION) ==
                    PackageManager.PERMISSION_GRANTED

        when {
            granted -> {
                findNavController().navigate(R.id.action_permission_fragment_to_driver_fragment)
            }
            shouldShowRequestPermissionRationale(PERMISSION) -> {
                Snackbar.make(requireView(), "This app requires permission to access your location.", Snackbar.LENGTH_INDEFINITE).apply {
                    setAction("OK") {
                        relaunchPermissionRequest()
                    }
                }
            }
            else -> {
                requestPermissionLauncher.launch(PERMISSION)
            }
        }
    }

    private fun relaunchPermissionRequest() {
        requestPermissionLauncher.launch(PERMISSION)
    }
}