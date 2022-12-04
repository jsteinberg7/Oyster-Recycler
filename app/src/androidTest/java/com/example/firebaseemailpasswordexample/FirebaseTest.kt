package com.cmsc436.oysterrecycler

import Driver
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class FirebaseTest {
    @Test
    fun testFirebase() {
        var dataEngine = DataEngine("firebaseUser.uid")
        var driver: Driver = Driver("firebaseUser.uid", firstName = "first", lastName = "last", phone = "123456",
            email = "email", carMake = "test make", carModel = "test Model", activePickups = arrayOf(), completedPickups = arrayOf())
        dataEngine.createDriverFile(driver = driver)

    }
}