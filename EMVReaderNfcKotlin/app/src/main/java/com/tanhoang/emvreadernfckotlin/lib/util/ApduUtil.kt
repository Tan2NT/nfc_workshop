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
import java.io.ByteArrayOutputStream
import java.lang.Exception

/**
 * @author AliMertOzdemir
 * @class ApduUtil
 * @created 16.04.2020
 */
object ApduUtil {
    private val TAG = ApduUtil::class.java.name
    private val PSE = "1PAY.SYS.DDF01".toByteArray() // PSE for Contact
    private val PPSE = "2PAY.SYS.DDF01".toByteArray() // PPSE for Contactless
    private const val GPO_P1 = 0x00.toByte()
    private const val GPO_P2 = 0x00.toByte()
    fun selectPse(): ByteArray? {
        // Returning result
        return selectPse(PSE)
    }

    fun selectPpse(): ByteArray? {
        // Returning result
        return selectPse(PPSE)
    }

    // PSE (Proximity Payment System Environment)
    fun selectPse(pse: ByteArray?): ByteArray? {
        // Returning result
        var result: ByteArray? = null
        // - Returning result
        var pseByteArrayOutputStream: ByteArrayOutputStream? = null
        try {
            pseByteArrayOutputStream = ByteArrayOutputStream()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        if (pseByteArrayOutputStream != null) {
            try {
                pseByteArrayOutputStream.write(TlvTagConstant.SELECT)
                pseByteArrayOutputStream.write(
                    byteArrayOf(
                        0x04.toByte(),  // P1
                        0x00.toByte() // P2
                    )
                )
                if (pse != null) {
                    pseByteArrayOutputStream.write(
                        byteArrayOf(
                            pse.size.toByte() // Lc
                        )
                    )
                    pseByteArrayOutputStream.write(pse) // Data
                }
                pseByteArrayOutputStream.write(
                    byteArrayOf(
                        0x00.toByte() // Le
                    )
                )
                pseByteArrayOutputStream.close()
                result = pseByteArrayOutputStream.toByteArray()
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
        return result
    }

    // - PSE (Proximity Payment System Environment)
    fun selectApplication(aid: ByteArray): ByteArray? {
        var result: ByteArray? = null
        var byteArrayOutputStream: ByteArrayOutputStream? = null
        try {
            byteArrayOutputStream = ByteArrayOutputStream()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        if (byteArrayOutputStream != null) {
            try {
                byteArrayOutputStream.write(TlvTagConstant.SELECT)
                byteArrayOutputStream.write(
                    byteArrayOf(
                        0x04.toByte(),  // P1
                        0x00.toByte(),  // P2
                        aid.size.toByte() // Lc
                    )
                )
                byteArrayOutputStream.write(aid) // Data
                byteArrayOutputStream.write(
                    byteArrayOf(
                        0x00.toByte() // Le
                    )
                )
                byteArrayOutputStream.close()
                result = byteArrayOutputStream.toByteArray()
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
        return result
    }

    fun getProcessingOption(pdolConstructed: ByteArray): ByteArray? {
        var result: ByteArray? = null
        var byteArrayOutputStream: ByteArrayOutputStream? = null
        try {
            byteArrayOutputStream = ByteArrayOutputStream()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        if (byteArrayOutputStream != null) {
            try {
                byteArrayOutputStream.write(TlvTagConstant.GET_PROCESSING_OPTIONS)
                byteArrayOutputStream.write(
                    byteArrayOf(
                        GPO_P1,  // P1
                        GPO_P2,  // P2
                        pdolConstructed.size.toByte() // Lc
                    )
                )
                byteArrayOutputStream.write(pdolConstructed) // Data
                byteArrayOutputStream.write(
                    byteArrayOf(
                        0x00.toByte() // Le
                    )
                )
                byteArrayOutputStream.close()

                // Temporary result
                val tempResult = byteArrayOutputStream.toByteArray()
                /// - Temporary result
                if (isGpoCommand(tempResult)) {
                    result = tempResult
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
        return result
    }

    fun getReadTlvData(tlvTag: ByteArray?): ByteArray? {
        // Returning result
        var result: ByteArray? = null
        // - Returning result
        var byteArrayOutputStream: ByteArrayOutputStream? = null
        try {
            byteArrayOutputStream = ByteArrayOutputStream()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        if (byteArrayOutputStream != null) {
            try {
                byteArrayOutputStream.write(TlvTagConstant.GET_DATA)
                byteArrayOutputStream.write(tlvTag)
                byteArrayOutputStream.write(
                    byteArrayOf(
                        0x00.toByte() // Le
                    )
                )
                byteArrayOutputStream.close()
                result = byteArrayOutputStream.toByteArray()
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
        return result
    }

    fun verifyPin(pin: ByteArray?): ByteArray? {
        var pin = pin
        var result: ByteArray? = null
        pin = byteArrayOf(
            0x24.toByte(),
            0x12.toByte(),
            0x34.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte()
        )

        //00 20 00 80 08 24 12 34 FF FF FF FF FF
        var byteArrayOutputStream: ByteArrayOutputStream? = null
        try {
            byteArrayOutputStream = ByteArrayOutputStream()
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        if (byteArrayOutputStream != null) {
            try {
                byteArrayOutputStream.write(TlvTagConstant.VERIFY)
                byteArrayOutputStream.write(
                    byteArrayOf(
                        0x00.toByte(),  // P1
                        0x80.toByte(),  // P2
                        pin.size.toByte() // Lc
                    )
                )
                byteArrayOutputStream.write(pin) // Pin
                byteArrayOutputStream.close()
                result = byteArrayOutputStream.toByteArray()
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
        return result
    }

    private fun isGpoCommand(commandApdu: ByteArray): Boolean {
        return commandApdu.size > 4
                && commandApdu[0] == TlvTagConstant.GET_PROCESSING_OPTIONS[0]
                && commandApdu[1] == TlvTagConstant.GET_PROCESSING_OPTIONS[1]
                && commandApdu[2] == GPO_P1 && commandApdu[3] == GPO_P2
    }
}