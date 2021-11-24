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
import com.tanhoang.emvreadernfckotlin.lib.model.ApduResponse
import java.util.*

/**
 * @author AliMertOzdemir
 * @class AidUtil
 * @created 16.04.2020
 */
object AidUtil {
    /*
        Mastercard (PayPass)
        RID: A000000004
        PIX: 1010
        AID (Application Identifier): A0000000041010
     */
    private val A0000000041010 = byteArrayOf(
        0xA0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x04.toByte(),
        0x10.toByte(),
        0x10.toByte()
    )

    /*
        Maestro (PayPass)
        RID: A000000004
        PIX: 3060
        AID (Application Identifier): A0000000043060
     */
    private val A0000000043060 = byteArrayOf(
        0xA0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x04.toByte(),
        0x30.toByte(),
        0x60.toByte()
    )

    /*
        Visa (PayWave)
        RID: A000000003
        PIX: 1010
        AID (Application Identifier): A0000000031010
     */
    private val A0000000031010 = byteArrayOf(
        0xA0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x03.toByte(),
        0x10.toByte(),
        0x10.toByte()
    )

    /*
        Visa Electron (PayWave)
        RID: A000000003
        PIX: 2010
        AID (Application Identifier): A0000000032010
     */
    private val A0000000032010 = byteArrayOf(
        0xA0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x03.toByte(),
        0x20.toByte(),
        0x10.toByte()
    )

    /*
        American Express
        RID: A000000025
        PIX: 0104
        AID (Application Identifier): A0000000250104
    */
    private val A0000000250104 = byteArrayOf(
        0xA0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x25.toByte(),
        0x01.toByte(),
        0x04.toByte()
    )

    /*
        TROY
        RID: A0000006
        PIX: 723010
        AID (Application Identifier): A0000006723010
    */
    private val A0000006723010 = byteArrayOf(
        0xA0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x06.toByte(),
        0x72.toByte(),
        0x30.toByte(),
        0x10.toByte()
    )

    /*
    TROY
    RID: A0000006
    PIX: 723020
    AID (Application Identifier): A0000006723020
*/
    private val A0000006723020 = byteArrayOf(
        0xA0.toByte(),
        0x00.toByte(),
        0x00.toByte(),
        0x06.toByte(),
        0x72.toByte(),
        0x30.toByte(),
        0x20.toByte()
    )

    fun isApprovedAID(aid: ByteArray?): Boolean {
        val shortAid = Arrays.copyOfRange(aid, 0, 7)
        return if (Arrays.equals(
                A0000000041010,
                shortAid
            )
        ) true else if (Arrays.equals(
                A0000000043060,
                shortAid
            )
        ) true else if (Arrays.equals(
                A0000000031010,
                shortAid
            )
        ) true else if (Arrays.equals(
                A0000000032010,
                shortAid
            )
        ) true else if (Arrays.equals(
                A0000000250104,
                shortAid
            )
        ) true else if (Arrays.equals(
                A0000006723010,
                shortAid
            )
        ) true else if (Arrays.equals(A0000006723020, shortAid)) true else false
    }

    fun getCardBrandByAID(aid: ByteArray?): CardType {
        val shortAid = Arrays.copyOfRange(aid, 0, 7)
        return if (Arrays.equals(
                A0000000041010,
                shortAid
            )
        ) CardType.MC else if (Arrays.equals(
                A0000000043060,
                shortAid
            )
        ) CardType.MC else if (Arrays.equals(
                A0000000031010,
                shortAid
            )
        ) CardType.VISA else if (Arrays.equals(
                A0000000032010,
                shortAid
            )
        ) CardType.VISA else if (Arrays.equals(
                A0000000250104,
                shortAid
            )
        ) CardType.AMEX else if (Arrays.equals(
                A0000006723010,
                shortAid
            )
        ) CardType.TROY else if (Arrays.equals(
                A0000006723020,
                shortAid
            )
        ) CardType.TROY else CardType.UNKNOWN
    }
}