package com.tanhoang.emvreadernfckotlin.lib.util

import android.app.ProgressDialog
import android.app.Activity
import android.content.DialogInterface
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.tanhoang.emvreadernfckotlin.lib.CtlessCardService.ResultListener
import com.tanhoang.emvreadernfckotlin.ui.MainActivity
import android.widget.LinearLayout
import com.tanhoang.emvreadernfckotlin.lib.CtlessCardService
import android.os.Bundle
import com.tanhoang.emvreadernfckotlin.R
import com.tanhoang.emvreadernfckotlin.lib.enums.BeepType
import com.tanhoang.emvreadernfckotlin.ui.util.AppUtils
import android.content.Intent
import com.tanhoang.emvreadernfckotlin.ui.ApduLogActivity
import android.media.ToneGenerator
import android.media.AudioManager
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.text.Html
import com.tanhoang.emvreadernfckotlin.lib.model.AflObject
import com.tanhoang.emvreadernfckotlin.lib.util.AflUtil
import com.tanhoang.emvreadernfckotlin.lib.util.TlvTagConstant
import com.tanhoang.emvreadernfckotlin.lib.util.AidUtil
import com.tanhoang.emvreadernfckotlin.lib.enums.CardType
import com.tanhoang.emvreadernfckotlin.lib.model.TlvObject
import com.tanhoang.emvreadernfckotlin.lib.util.GpoUtil
import com.tanhoang.emvreadernfckotlin.lib.util.HexUtil
import com.tanhoang.emvreadernfckotlin.lib.util.TlvUtil
import com.tanhoang.emvreadernfckotlin.lib.util.ApduUtil
import com.tanhoang.emvreadernfckotlin.lib.util.CardUtil
import com.tanhoang.emvreadernfckotlin.lib.util.SharedPrefUtil
import android.os.Parcelable
import android.os.Parcel
import android.os.Parcelable.Creator
import kotlin.Throws
import android.nfc.NfcAdapter.ReaderCallback
import android.nfc.NfcAdapter
import android.nfc.tech.IsoDep
import android.os.CountDownTimer
import android.widget.Toast
import android.nfc.TagLostException
import android.util.Log
import com.tanhoang.emvreadernfckotlin.lib.model.ApduResponse
import java.lang.Exception
import java.lang.StringBuilder

/**
 * @author AliMertOzdemir
 * @class HexUtil
 * @created 16.04.2020
 */
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
                    result[i / 2] = ((Character.digit(hexadecimal[i], 16) shl 4) + Character.digit(
                        hexadecimal[i + 1], 16
                    )).toByte()
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