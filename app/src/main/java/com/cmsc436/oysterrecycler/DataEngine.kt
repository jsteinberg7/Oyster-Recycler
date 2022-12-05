package com.cmsc436.oysterrecycler

import Driver
import Pickup
import Restaurant
import android.content.ContentValues.TAG
import android.util.Log
import androidx.fragment.app.activityViewModels
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.tasks.await
import androidx.fragment.app.activityViewModels
import com.google.firebase.Timestamp

class DataEngine() {

    val firestore = Firebase.firestore
    var driversCollection = firestore.collection("drivers")
    var restaurantsCollection = firestore.collection("restaurants")
    var activePickupsCollection = firestore.collection("activePickups")
    var completedPickupsCollection = firestore.collection("completedPickups")

    
    /* Driver functions */
    fun createDriverFile(driver: Driver) {
        val map = driver.serialize()
        
        driversCollection
        .document(driver.UID)
            .set(map)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    fun updateDriverFile(driver: Driver) {
        driversCollection
            .document(driver.UID)
            .set(driver.serialize())
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    fun getDriverByUID(UID: String): Driver {
        var driver = Driver("","","","","","","", listOf(), listOf())
        driversCollection
            .document(UID)
            .get()
            .addOnSuccessListener { document ->
                Log.i("test", document.toString())
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    driver = Driver(
                        document.data?.get("UID").toString(),
                        document.data?.get("firstname").toString(),
                        document.data?.get("lastname").toString(),
                        document.data?.get("email").toString(),
                        document.data?.get("car_license_plate").toString(),
                        document.data?.get("car_make").toString(),
                        document.data?.get("car_color").toString(),
                        document.data?.get("active_pickups") as List<String>,
                        document.data?.get("completed_pickups") as List<String>

                    )
                } else {
                    Log.d("test", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("test", "get failed with ", exception)
            }
        Log.i("test", "returning")
        return driver
    }
     
    /* Restaurant functions */
    fun createRestaurantFile(restaurant: Restaurant){
        val map = restaurant.serialize()
       
        restaurantsCollection
        .document(restaurant.UID)
            .set(map)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    fun getRestaurantByUID(UID: String): Restaurant {
        var restaurant: Restaurant = Restaurant("","","","","", listOf(), listOf())
        restaurantsCollection
            .document(UID)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    restaurant = Restaurant(
                        UID = document.data?.get("UID").toString(),
                        name = document.data?.get("name").toString(),
                        email = document.data?.get("email").toString(),
                        phone = document.data?.get("phone").toString(),
                        address = document.data?.get("address").toString(),
                        activePickups = document.data?.get("active_pickups") as List<String>,
                        completedPickups = document.data?.get("completed_pickups") as List<String>
                    )
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        return restaurant
    }

    fun getRestaurantByName(Name: String): Restaurant {
        var restaurant: Restaurant = Restaurant("","","","","", listOf(), listOf())
        restaurantsCollection.whereEqualTo("name", Name).get()
            .addOnSuccessListener { documents ->
                var document = documents.elementAt(0)
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    restaurant = Restaurant(
                        UID = document.data?.get("UID").toString(),
                        name = document.data?.get("name").toString(),
                        email = document.data?.get("email").toString(),
                        phone = document.data?.get("phone").toString(),
                        address = document.data?.get("address").toString(),
                        activePickups = document.data?.get("active_pickups") as List<String>,
                        completedPickups = document.data?.get("completed_pickups") as List<String>

                    )
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        return restaurant
    }


    fun updateRestaurantFile(restaurant: Restaurant) {
        driversCollection
        .document(restaurant.UID)
            .set(restaurant.serialize())
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    fun getRecentCompletedPickups(restaurant: Restaurant, num_pickups: Int): ArrayList<Pickup> {
        var recentPickups: ArrayList<Pickup> = ArrayList()
        for (pickup in restaurant.completedPickups) {
            completedPickupsCollection
            .document(pickup)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        recentPickups.add(Pickup(
                        UID = document.data?.get("UID").toString(),
                        restaurantID = document.data?.get("restaurantID").toString(),
                        driverID = document.data?.get("driverID").toString(),
                        when_date = document.data?.get("when_date").toString()
                    ))
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }

        return recentPickups 
    }

    fun getActivePickup(restaurantID: String): ArrayList<Pickup> {
        var pickupList: ArrayList<Pickup> = ArrayList()
        // get pickup from pickups collection. Document ID is restaurantID. Add pickup to pickupList
        activePickupsCollection
            .document(restaurantID)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    pickupList.add(Pickup(
                        UID = document.data?.get("UID").toString(),
                        restaurantID = document.data?.get("restaurantID").toString(),
                        driverID = document.data?.get("driverID").toString(),
                        when_date = document.data?.get("when_date").toString()
                    ))
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        return pickupList
    }

    /* Pickup functions */
//    fun initializePickup(pickup: Pickup) {
//        // 1. Create pickup file and push to Firebase
//        createPickupFile(pickup).then(
//            val restaurant = getRestaurantByUID(pickup.restaurantID).then(
//                // 2. Add pickup to restaurant's active pickups
//                addActivePickupToRestaurant(restaurant, pickup.UID)
//            )
//        )
//
//    }

    fun createPickupFile(pickup: Pickup) {
        val map = HashMap<String, Any>()
        map["UID"] = pickup.UID
        map["restaurant_id"] = pickup.restaurantID
        map["driver_id"] = pickup.driverID
        map["when"] = pickup.when_date

        activePickupsCollection
        .document(pickup.UID)
            .set(map)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    fun addActivePickupToRestaurant(restaurant: Restaurant, pickup: Pickup) {
        restaurant.activePickups += pickup.UID
        updateRestaurantFile(restaurant)
    }

    
    fun updatePickupFile(pickup: Pickup) {
        activePickupsCollection
        .document(pickup.UID)
            .set(pickup.serialize())
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }


}