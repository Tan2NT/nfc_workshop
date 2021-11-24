package com.tanhoang.emvreadernfckotlin.ui

import android.app.ProgressDialog
import android.app.Activity
import android.app.AlertDialog
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
import com.tanhoang.emvreadernfckotlin.lib.util.AflUtil
import com.tanhoang.emvreadernfckotlin.lib.util.TlvTagConstant
import com.tanhoang.emvreadernfckotlin.lib.util.AidUtil
import com.tanhoang.emvreadernfckotlin.lib.enums.CardType
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
import com.tanhoang.emvreadernfckotlin.lib.model.*
import java.util.ArrayList

class MainActivity : AppCompatActivity(), ResultListener {
    private val TAG = MainActivity::class.java.name
    private var llContainer: LinearLayout? = null
    private var mCtlessCardService: CtlessCardService? = null
    private var mProgressDialog: ProgressDialog? = null
    private var mAlertDialog: AlertDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        llContainer = findViewById(R.id.ctless_container)
        mCtlessCardService = CtlessCardService(this, this)
    }

    override fun onResume() {
        super.onResume()
        mCtlessCardService!!.start()
    }

    override fun onCardDetect() {
        Log.d(TAG, "ON CARD DETECTED")
        playBeep(BeepType.SUCCESS)
        showProgressDialog()
    }

    override fun onCardReadSuccess(card: Card?) {
        dismissProgressDialog()
        showCardDetailDialog(card)
    }

    override fun onCardReadFail(error: String) {
        playBeep(BeepType.FAIL)
        dismissProgressDialog()
        showAlertDialog("ERROR", error)
    }

    override fun onCardReadTimeout() {
        playBeep(BeepType.FAIL)
        dismissProgressDialog()
        AppUtils.showSnackBar(llContainer, "Timeout has been reached...", "OK")
    }

    override fun onCardMovedSoFast() {
        playBeep(BeepType.FAIL)
        dismissProgressDialog()
        AppUtils.showSnackBar(llContainer, "Please do not remove your card while reading...", "OK")
    }

    override fun onCardSelectApplication(applications: List<Application?>?) {
        playBeep(BeepType.FAIL)
        dismissProgressDialog()
        showApplicationSelectionDialog(applications)
    }

    private fun openApduLogDetail(logMessages: ArrayList<LogMessage?>) {
        val intent: Intent = ApduLogActivity.Companion.newIntent(this)
        intent.putParcelableArrayListExtra("apduLog", logMessages)
        startActivity(intent)
    }

    private fun playBeep(beepType: BeepType) {
        val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        when (beepType) {
            BeepType.SUCCESS -> toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
            BeepType.FAIL -> toneGen.startTone(ToneGenerator.TONE_SUP_ERROR, 200)
            else -> {
            }
        }
    }

    private fun showProgressDialog() {
        dismissAlertDialog()
        runOnUiThread {
            mProgressDialog = AppUtils.showLoadingDialog(
                this,
                "Reading Card",
                "Please do not remove your card while reading..."
            )
        }
    }

    private fun dismissProgressDialog() {
        runOnUiThread { mProgressDialog!!.dismiss() }
    }

    private fun showAlertDialog(title: String, message: String) {
        runOnUiThread {
            mAlertDialog = AppUtils.showAlertDialog(
                this,
                title,
                message,
                "OK",
                "SHOW APDU LOGS",
                false
            ) { dialogInterface: DialogInterface?, button: Int -> mAlertDialog!!.dismiss() }
        }
    }

    private fun showCardDetailDialog(card: Card?) {
        runOnUiThread {
            val title = "Card Detail"
            var message = """
                Card Brand : ${card?.cardType?.cardBrand}
                Card Pan : ${card?.pan}
                Card Expire Date : ${card?.expireDate}
                Card Track2 Data : ${card?.track2}
                
                """.trimIndent()
            if (!card?.emvData.isNullOrEmpty())
                message += "Card EmvData : " + card?.emvData
            mAlertDialog = AppUtils.showAlertDialog(
                this,
                title,
                message,
                "OK",
                "SHOW APDU LOGS",
                false
            ) { dialogInterface: DialogInterface?, button: Int ->
                when (button) {
                    DialogInterface.BUTTON_POSITIVE, DialogInterface.BUTTON_NEUTRAL -> {
                        mCtlessCardService!!.start()
                        mAlertDialog!!.dismiss()
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        if (!card?.logMessages.isNullOrEmpty()
                        ) openApduLogDetail(
                            ArrayList(card?.logMessages)
                        )
                        mAlertDialog!!.dismiss()
                    }
                }
            }
        }
    }

    private fun showApplicationSelectionDialog(applications: List<Application?>?) {
        val appNames = arrayOfNulls<String>(applications!!.size)
        var index = 0
        for (application in applications) {
            appNames[index] = application?.appLabel
            index++
        }
        runOnUiThread {
            val title = "Select One of Your Cards"
            mAlertDialog = AppUtils.showSingleChoiceListDialog(
                this,
                title,
                appNames,
                "OK"
            ) { dialogInterface: DialogInterface?, i: Int ->
                mCtlessCardService!!.setSelectedApplication(
                    i
                )
            }
        }
    }

    private fun dismissAlertDialog() {
        if (mAlertDialog != null) runOnUiThread { mAlertDialog!!.dismiss() }
    }
}