package com.ashcorp.libpinblock

enum class PinblockFormat(val marker: Byte, val minFill: Byte, val maxFill: Byte) {
    ISO_0(0, 0xF, 0xF),
    ISO_1(1, 0xF, 0xF),
    ISO_2(2, 0xF, 0xF),
    ISO_3(3,  0x0, 0xF)
}

interface IPinblock {
    fun encode(format: PinblockFormat, pin: String, pan: String?) : String
    fun decode(pinblock: String, pan: String): String
}