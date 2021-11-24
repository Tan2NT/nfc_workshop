package com.tanhoang.emvreadernfckotlin.lib.enums

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

/**
 * @author AliMertOzdemir
 * @class CardType
 * @created 21.04.2020
 */
enum class CardType(val cardBrand: String) {
    MC("MasterCard"), VISA("Visa"), AMEX("AmericanExpress"), TROY("Troy"), UNKNOWN("N/A");

}