package com.cmsc436.oysterrecycler

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cmsc436.oysterrecycler.databinding.MainActivityBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.provider.FirebaseInitProvider
import java.sql.Driver

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase APIs
        FirebaseInitProvider()

        // Use the provided ViewBinding class to inflate the
        // layout and set content view to the root view.
        setContentView(MainActivityBinding.inflate(layoutInflater).root)
    }
}
