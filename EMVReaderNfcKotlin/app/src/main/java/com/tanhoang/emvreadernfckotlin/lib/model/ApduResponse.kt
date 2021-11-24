package com.tanhoang.emvreadernfckotlin.lib.model

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
import com.tanhoang.emvreadernfckotlin.lib.model.ApduResponse
import java.security.AccessControlException

/**
 * @author AliMertOzdemir
 * @class ApduResponse
 * @created 16.04.2020
 */
class ApduResponse(respApdu: ByteArray) {
    var sW1 = 0x00
        protected set
    var sW2 = 0x00
        protected set
    var data = ByteArray(0)
        protected set
    protected var mBytes = ByteArray(0)
    val sW1SW2: Int
        get() = sW1 shl 8 or sW2

    fun toBytes(): ByteArray {
        return mBytes
    }

    @Throws(AccessControlException::class)
    fun checkLengthAndStatus(length: Int, sw1sw2: Int, message: String) {
        if (sW1SW2 != sw1sw2 || data.size != length) {
            throw AccessControlException(
                "ResponseApdu is wrong at "
                        + message
            )
        }
    }

    @Throws(AccessControlException::class)
    fun checkLengthAndStatus(
        length: Int, sw1sw2List: IntArray,
        message: String
    ) {
        if (data.size != length) {
            throw AccessControlException(
                "ResponseApdu is wrong at "
                        + message
            )
        }
        for (sw1sw2 in sw1sw2List) {
            if (sW1SW2 == sw1sw2) {
                return  // sw1sw2 matches => return
            }
        }
        throw AccessControlException("ResponseApdu is wrong at $message")
    }

    @Throws(AccessControlException::class)
    fun checkStatus(sw1sw2List: IntArray, message: String) {
        for (sw1sw2 in sw1sw2List) {
            if (sW1SW2 == sw1sw2) {
                return  // sw1sw2 matches => return
            }
        }
        throw AccessControlException("ResponseApdu is wrong at $message")
    }

    @Throws(AccessControlException::class)
    fun checkStatus(sw1sw2: Int, message: String) {
        if (sW1SW2 != sw1sw2) {
            throw AccessControlException(
                "ResponseApdu is wrong at "
                        + message
            )
        }
    }

    fun isStatus(sw1sw2: Int): Boolean {
        return if (sW1SW2 == sw1sw2) {
            true
        } else {
            false
        }
    }

    init {
        if (respApdu.size >= 2) {
            if(respApdu.size > 2) {
                data = ByteArray(respApdu.size - 2)
                System.arraycopy(respApdu, 0, data, 0, respApdu.size - 2)
            }
            sW1 = 0x00FF and respApdu[respApdu.size - 2].toInt()
            sW2 = 0x00FF and respApdu[respApdu.size - 1].toInt()
            mBytes = respApdu
        }
    }
}