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
import com.tanhoang.emvreadernfckotlin.lib.model.Card
import java.util.regex.Pattern

/**
 * @author AliMertOzdemir
 * @class CardUtil
 * @created 21.04.2020
 */
object CardUtil {
    private val TRACK2_EQUIVALENT_PATTERN =
        Pattern.compile("([0-9]{1,19})D([0-9]{4})([0-9]{3})?(.*)")

    fun getCardInfoFromTrack2(track2: ByteArray): Card {
        val card = Card()
        val track2Data = HexUtil.bytesToHexadecimal(track2)
        var cardNumber = ""
        var expireDate = ""
        val service = ""
        if (track2Data != null) {
            val matcher = TRACK2_EQUIVALENT_PATTERN.matcher(track2Data)
            if (matcher.find()) {
                cardNumber = matcher.group(1)
                expireDate = matcher.group(2)
                if (expireDate != null) {
                    expireDate = expireDate.substring(2, 4) + expireDate.substring(0, 2)
                }
            }
        }
        card.track2 = track2Data
        card.pan = cardNumber
        card.expireDate = expireDate
        return card
    }
}