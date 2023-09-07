package com.ashcorp.libpinblock

import kotlin.experimental.xor
import kotlin.random.Random

/**
 * This class manages the encoding and decoding of pin blocks
 *
 * */
class Pinblock: IPinblock {

    /**
     * Encodes [pin] and [pan] into a pin block based on [format]
     * @return the hex string of the pin block.
     */
    override fun encode(format: PinblockFormat, pin: String, pan: String?): String {
        if (pin.length < MINPIN || pin.length > MAXPIN) {
            throw Exception("Pin must be between {$MINPIN} and {$MAXPIN} digits")
        }
        for (digit in pin) {
            if (!digit.isDigit()) {
                throw Exception("Pin must be numeric")
            }
        }
        return when (format) {
            PinblockFormat.ISO_0,
            PinblockFormat.ISO_3 -> {
                pan?.let {
                    encodeISO(format, pin, it)
                } ?: throw Exception("{$format} requires a valid PAN")
            } else -> {
                throw Exception("Algorithm not yet implemented")
            }
        }
    }

    /**
     * Decodes a hex string [pinblock] and [pan] back into a pin
     * @return the string of the pin.
     */
    override fun decode(pinblock: String, pan: String): String {
        return when(pinblock.first().digitToInt().toByte()) {
            PinblockFormat.ISO_0.marker,
            PinblockFormat.ISO_3.marker -> {
                decodeISO(pinblock, pan)
            } else -> {
                throw Exception("Algorithm not yet implemented")
            }
        }
    }

    private fun encodeISO(format: PinblockFormat, pin: String, pan: String): String {
        val pinNibbles = ByteArray(NUMNIBBLES)
        val panNibbles = ByteArray(NUMNIBBLES)
        val pinblockNibbles = ByteArray(NUMNIBBLES)
        pinNibbles[0] = format.marker
        pinNibbles[1] = pin.length.toByte()
        var pinBytesIndex = 2
        for (pinChar in pin) {
            pinNibbles[pinBytesIndex] = pinChar.digitToInt().toByte()
            pinBytesIndex++
        }
        if (format.maxFill == format.minFill) {
            while (pinBytesIndex < NUMNIBBLES) {
                pinNibbles[pinBytesIndex] = format.minFill
                pinBytesIndex++
            }
        } else {
            while (pinBytesIndex < NUMNIBBLES) {
                pinNibbles[pinBytesIndex] =
                    Random.nextInt(format.minFill.toInt(), format.maxFill.toInt()).toByte()
                pinBytesIndex++
            }
        }
        var panBytesIndex = 4
        for (panChar in pan.substring(pan.length-13, pan.length-1)) {
            panNibbles[panBytesIndex] = panChar.digitToInt().toByte()
            panBytesIndex++
            if (panBytesIndex == NUMNIBBLES) {
                break
            }
        }
        for (pinBlockIndex in 0 until NUMNIBBLES) {
            pinblockNibbles[pinBlockIndex] = pinNibbles[pinBlockIndex].xor(panNibbles[pinBlockIndex])
        }
        return pinblockNibbles.joinToString("") { byte -> "%X".format(byte) }
    }

    private fun charToNibble(char: Char) : Byte {
        val values = "0123456789ABCDEF"
        return values.indexOf(char).toByte()
    }

    private fun decodeISO(pinblock: String, pan: String): String {
        val panNibbles = ByteArray(NUMNIBBLES)
        val pinblockNibbles = pinblock.map {
                charToNibble(it)
            }.toByteArray()
        val pinNibbles = ByteArray(NUMNIBBLES)
        var panNibblesIndex = 4
        for (panChar in pan.substring(pan.length-13, pan.length-1)) {
            panNibbles[panNibblesIndex] = panChar.digitToInt().toByte()
            panNibblesIndex++
            if (panNibblesIndex == NUMNIBBLES) {
                break
            }
        }
        for (pinBlockIndex in 0 until NUMNIBBLES) {
            pinNibbles[pinBlockIndex] = pinblockNibbles[pinBlockIndex].xor(panNibbles[pinBlockIndex])
        }
        val pinLength = pinNibbles[1].toInt()
        return pinNibbles.joinToString("") { byte -> "%X".format(byte) }.substring(2, 2 + pinLength)
    }

    companion object {
        const val MINPIN = 4
        const val MAXPIN = 12
        const val NUMNIBBLES = 16
    }
}