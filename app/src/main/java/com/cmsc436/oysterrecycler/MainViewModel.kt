package com.cmsc436.oysterrecycler

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel


class MainViewModel(application: Application) : AndroidViewModel(application) {

    var curDriverID = ""
    var curRestaurantID = ""
    private val sharedPref = application.getSharedPreferences("preference_key", Context.MODE_PRIVATE)

    fun initializeLocalValues() {
        curDriverID = sharedPref.getString("driverID", "")!!
        curRestaurantID = sharedPref.getString("restaurantID", "")!!
    }

    fun updateDriverID(ID: String) {
        curDriverID = ID
        with(sharedPref.edit()) {
            putString("driverID", ID)
            apply()
        }
    }

    fun updateRestaurantID(ID: String) {
        curRestaurantID = ID
        with(sharedPref.edit()) {
            putString("restaurantID", ID)
            apply()
        }
    }

    fun clearViewModel() {
        with(sharedPref.edit()) {
            putString("restaurantID", "")
            curRestaurantID = ""
            putString("driverID", "")
            curDriverID = ""
            apply()
        }
    }

    fun isSignedInAsDriver(): Boolean {
        return curDriverID!!.isNotEmpty() || sharedPref!!.getString("driverID", null)!!.isNotEmpty()
    }

    fun isSignedInAsRestaurant(): Boolean {
        return curRestaurantID!!.isNotEmpty() || sharedPref!!.getString("restaurantID", null)!!.isNotEmpty()
    }
    
}