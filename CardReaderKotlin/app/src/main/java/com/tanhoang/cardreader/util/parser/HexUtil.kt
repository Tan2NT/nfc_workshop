package com.tanhoang.cardreader.util.parser

import android.util.Log

object HexUtil {
    private val TAG = HexUtil::class.java.name
    fun getSpaces(length: Int): String {
        val buf = StringBuilder(length)
        for (i in 0 until length) {
            buf.append(" ")
        }
        return buf.toString()
    }

    fun bytesToHexadecimal(bytesIn: ByteArray): String? {
        // Returning result
        var result: String? = null
        // - Returning result
        var stringBuilder: StringBuilder? = null
        try {
            stringBuilder = StringBuilder()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        if (stringBuilder != null) {
            for (byteOut in bytesIn) {
                try {
                    stringBuilder.append(String.format("%02X", byteOut))
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
            }
            try {
                result = stringBuilder.toString()
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
        return result
    }

    fun hexadecimalToBytes(hexadecimal: String): ByteArray? {
        // Returning result
        var result: ByteArray? = null
        // - Returning result
        try {
            result = ByteArray(hexadecimal.length / 2)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        if (result != null) {
            var i = 0
            while (i < hexadecimal.length) {
                try {
                    result[i / 2] = (
                        (Character.digit(hexadecimal[i], 16) shl 4) + Character.digit(
                            hexadecimal[i + 1], 16
                        )
                        ).toByte()
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
                i += 2
            }
        }
        return result
    }

    fun hexadecimalToAscii(hexadecimal: String): String? {
        // Returning result
        var result: String? = null
        // - Returning result
        var stringBuilder: StringBuilder? = null
        try {
            stringBuilder = StringBuilder()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        if (stringBuilder != null) {
            var i = 0
            while (i < hexadecimal.length) {
                try {
                    stringBuilder.append(hexadecimal.substring(i, i + 2).toInt(16).toChar())
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
                i += 2
            }
            try {
                result = stringBuilder.toString()
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
        return result
    }

    fun bytesToAscii(bytesIn: ByteArray): String? {
        // Returning result
        var result: String? = null
        // - Returning result
        val hexadecimal = bytesToHexadecimal(bytesIn)
        if (hexadecimal != null) {
            result = hexadecimalToAscii(hexadecimal)
        }
        return result
    }
}
