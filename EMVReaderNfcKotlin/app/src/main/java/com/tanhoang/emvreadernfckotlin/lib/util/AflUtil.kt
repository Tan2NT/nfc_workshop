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
import java.util.ArrayList

/**
 * @author AliMertOzdemir
 * @class AflUtil
 * @created 17.04.2020
 */
object AflUtil {
    private val TAG = AflUtil::class.java.name
    fun getAflDataRecords(aflData: ByteArray): List<AflObject?>? {
        // Returning result
        var result: MutableList<AflObject?>? = null
        // - Returning result
        Log.d(TAG, "AFL Data Length: " + aflData.size)
        if (aflData.size < 4) { // At least 4 bytes length needed to go ahead
            try {
                throw Exception("Cannot preform AFL data byte array actions, available bytes < 4; Length is " + aflData.size)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        } else {
            result = ArrayList()
            for (i in 0 until aflData.size / 4) {
                var firstRecordNumber = aflData[4 * i + 1].toInt()
                val lastRecordNumber =
                    aflData[4 * i + 2].toInt() // First record number & final record number
                while (firstRecordNumber <= lastRecordNumber) {
                    val aflObject = AflObject(
                        sfi = aflData[4 * i].toInt() shr 3,
                        recordNumber = firstRecordNumber,
                        readCommand = null
                    )
                    var cReadRecord: ByteArray? = null
                    var readRecordByteArrayOutputStream: ByteArrayOutputStream? = null
                    try {
                        readRecordByteArrayOutputStream = ByteArrayOutputStream()
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                    if (readRecordByteArrayOutputStream != null) {
                        try {
                            readRecordByteArrayOutputStream.write(TlvTagConstant.READ_RECORD)
                            readRecordByteArrayOutputStream.write(
                                byteArrayOf(
                                    aflObject.recordNumber.toByte(),
                                    (aflObject.sfi shl 0x03 or 0x04).toByte()
                                )
                            )
                            readRecordByteArrayOutputStream.write(
                                byteArrayOf(
                                    0x00.toByte() // Le
                                )
                            )
                            readRecordByteArrayOutputStream.close()
                            cReadRecord = readRecordByteArrayOutputStream.toByteArray()
                        } catch (e: Exception) {
                            Log.e(TAG, e.toString())
                        }
                    }
                    if (cReadRecord != null) {
                        aflObject.readCommand = cReadRecord
                    }
                    result.add(aflObject)
                    firstRecordNumber++
                }
            }
        }
        return result
    }
}