package com.binay.shaw.justap.utilities

import com.binay.shaw.justap.utilities.Validator.Companion.isValidEmail
import org.junit.Assert.*
import org.junit.Test


class ValidatorTest {

    @Test
    fun `email-is-empty` () {
        val email = ""
        val result = email.isValidEmail(false)
        assertEquals(false, result)
    }

    @Test
    fun `email-is-not-empty` () {
        val email = "binayshaw7777@gmail.com"
        val result = email.isValidEmail(false)
        assertEquals(true, result)
    }

    @Test
    fun `email-is-not-empty-but-number-is-missing` () {
        val email = "binayshaw@gmail.com"
        val result = email.isValidEmail(false)
        assertEquals(true, result)
    }

    @Test
    fun `email-is-not-empty-but-symbol-is-missing` () {
        val email = "binayshaw7777gmail.com"
        val result = email.isValidEmail(false)
        assertEquals(false, result)
    }

    @Test
    fun `email-is-not-empty-but-dot-com-is-missing` () {
        val email = "binayshaw7777@gmail"
        val result = email.isValidEmail(false)
        assertEquals(false, result)
    }

    @Test
    fun `email-is-not-empty-but-prefix-name-is-missing` () {
        val email = "7777@gmail.com"
        val result = email.isValidEmail(false)
        assertEquals(false, result)
    }


}