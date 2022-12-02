package com.cmsc436.oysterrecycler

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ktx.firestore

class DataEngine {


    val firestore = Firebase.firestore

    fun createDriverFiles(){
        val map = HashMap<String, Any>()
        map["name"] = "test"
        map["age"] = 15
        map["address"] = "test"
        map["mobile_no"] = 231234423
        firestore.collection("drivers").document("driver")
            .set(map)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

}