package com.cmsc436.oysterrecycler

import Driver
import Pickup
import Restaurant
import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ktx.firestore

class DataEngine(UID: String) {

    val firestore = Firebase.firestore
    var driversCollection = firestore.collection("drivers")
    var restaurantsCollection = firestore.collection("restaurants")
    var pickupsCollection = firestore.collection("pickups")

    val UID: String = UID
    
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
     
    /* Restaurant functions */
    fun createRestaurantFile(restaurant: Restaurant){
        val map = restaurant.serialize()
       
        restaurantsCollection
        .document(restaurant.UID)
            .set(map)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    fun getRestaurantFile(): Restaurant {
        var restaurant: Restaurant = Restaurant("","","","","","","", emptyArray(), emptyArray())
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
                        carMake = document.data?.get("car_make").toString(),
                        carModel = document.data?.get("car_model").toString(),
                        activePickups = document.data?.get("active_pickups") as Array<String>,
                        completedPickups = document.data?.get("completed_pickups") as Array<String>

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

    fun getRecentPickups(restaurant: Restaurant, num_pickups: Int): Array<Pickup> {
        var recentPickups: Array<Pickup> = emptyArray()
        
        for (pickup in restaurant.completedPickups) {
            pickupsCollection
            .document(pickup)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                        recentPickups += document.data as Pickup
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

    /* Pickup functions */

    fun createPickupFile(pickup: Pickup) {
        val map = HashMap<String, Any>()
        map["UID"] = pickup.UID
        map["restaurantID"] = pickup.restaurantID
        map["driverID"] = pickup.driverID
        map["dateCreated"] = pickup.dateCreated
        map["quantity"] = pickup.quantity

        pickupsCollection
        .document(pickup.UID)
            .set(map)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

//    fun getPickupFile(pickupID: String): Pickup {
//
//    }
    
    fun updatePickupFile(pickup: Pickup) {
        pickupsCollection
        .document(pickup.UID)
            .set(pickup.serialize())
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }


}