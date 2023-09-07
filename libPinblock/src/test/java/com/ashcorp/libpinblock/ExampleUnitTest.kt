package com.ashcorp.libpinblock

import org.junit.Test

import org.junit.Assert.*

class ExampleUnitTest {

    companion object {
        private val pinblock = Pinblock()
        private const val pan = "1111222233334444"
        private const val tooShortPin = "123"
        private const val minPin = "1234"
        private const val maxPin = "123456789012"
        private const val tooLongPin = "12345678912345"
        private const val nonNumericPin = "1a2b"
    }

    @Test(expected = Exception::class)
    fun givenToShortPinLength_thenThrowsException() {
        pinblock.encode(PinblockFormat.ISO_3, tooShortPin, pan)
    }

    @Test(expected = Exception::class)
    fun givenToLongPinLength_thenThrowsException() {
        pinblock.encode(PinblockFormat.ISO_3, tooLongPin, pan)
    }

    @Test(expected = Exception::class)
    fun givenNonNumericPin_thenThrowsException() {
        pinblock.encode(PinblockFormat.ISO_3, nonNumericPin, pan)
    }

    @Test(expected = Exception::class)
    fun givenNullPan_thenThrowsException() {
        pinblock.encode(PinblockFormat.ISO_3, tooLongPin, null)
    }

    @Test
    fun givenMinPinLength_thenEncodeDecodeISO3OK() {
        val encodedPinblock = pinblock.encode(PinblockFormat.ISO_3,  minPin, pan)
        val decodedPin =  pinblock.decode(encodedPinblock, pan)
        assertEquals(minPin, decodedPin)
    }

    @Test
    fun givenMaxPinLength_thenEncodeDecodeISO3OK() {
        val encodedPinblock = pinblock.encode(PinblockFormat.ISO_3,  maxPin, pan)
        val decodedPin =  pinblock.decode(encodedPinblock, pan)
        assertEquals(maxPin, decodedPin)
    }

    @Test
    fun givenMinPinLength_thenEncodeDecodeISO0OK() {
        val encodedPinblock = pinblock.encode(PinblockFormat.ISO_0, minPin, pan)
        val decodedPin =  pinblock.decode(encodedPinblock, pan)
        assertEquals(minPin, decodedPin)
    }

    @Test
    fun givenKnownPinblockForPinAndPan_thenEncodeISO0OK() {
        assertEquals("0412AC89ABCDEF67", pinblock.encode(PinblockFormat.ISO_0, "1234", "43219876543210987"))
        assertEquals("041215FEDCBA9876", pinblock.encode(PinblockFormat.ISO_0, "1234", "5432101234567891"))
    }

}