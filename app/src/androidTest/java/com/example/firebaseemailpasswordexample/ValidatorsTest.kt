package com.example.firebaseemailpasswordexample

import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class ValidatorsTest {
    @Test
    fun testValidator() {
        val validator = Validators()

        // test invalid emails
        assertFalse(validator.validEmail("#@%^%#\$@#\$@#.com"))
        assertFalse(validator.validEmail("@example.com"))
        assertFalse(validator.validEmail("email.example.com"))
        assertFalse(validator.validEmail("email.@example.com"))
        assertFalse(validator.validEmail("email@example"))

        // test valid emails
        assertTrue(validator.validEmail("firstname-lastname@example.com"))
        assertTrue(validator.validEmail("_______@example.com"))
        assertTrue(validator.validEmail("\"email\"@example.com"))
        assertTrue(validator.validEmail("email@example.com"))
        assertTrue(validator.validEmail("firstname.lastname@example.com"))
    }
}