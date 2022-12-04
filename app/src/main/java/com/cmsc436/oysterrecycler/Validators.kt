package com.cmsc436.oysterrecycler

import androidx.core.text.isDigitsOnly

class Validators {
    fun validEmail(email: String?) : Boolean {
        if (email.isNullOrEmpty()) {
            return false
        }

        // Trim empty spaces from email
       // val trimmedEmail = email.toString().trim() { it <= ' '}

       // if (trimmedEmail.isNullOrEmpty()) {
       //     return false
       // }

        // General Email Regex (RFC 5322 Official Standard)
        val emailRegex = Regex("\\S+@\\S+\\.\\S+")
        return emailRegex.matches(email)
    }

    fun validPassword(password: String?) : Boolean {
        if (password.isNullOrEmpty()) {
            return false
        }

        // Min 8 char, 1 letter, 1 number, 1 Special character (!@#$&*)
        val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*[!@#\$&*])(?=.*\\d)[A-Za-z!@#\$&*\\d]{8,}\$")
        return passwordRegex.matches(password)
    }

    fun validColor(vehicleColor: String?) : Boolean {
        if (vehicleColor.isNullOrEmpty()) return false

        return true
    }

    fun validMake(vehicleMake: String?) : Boolean {
        if (vehicleMake.isNullOrEmpty()) return false
        else if (vehicleMake.isDigitsOnly()) return false

        return true
    }

    // Creates a hash of the user's email when logging in
    fun emailHash(email: String?) : Int {
        return email.hashCode()
    }

}