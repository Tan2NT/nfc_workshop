package com.tanhoang.emvreadernfckotlin.ui

import android.app.ProgressDialog
import android.app.Activity
import android.content.Context
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
import android.view.View
import android.widget.Button
import com.tanhoang.emvreadernfckotlin.lib.model.ApduResponse
import com.tanhoang.emvreadernfckotlin.lib.model.LogMessage
import java.lang.StringBuilder
import java.util.ArrayList

class ApduLogActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = ApduLogActivity::class.java.name
    private var tvResult: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apdu_log)
        tvResult = findViewById(R.id.tv_result)
        val btnOk = findViewById<Button>(R.id.button_ok)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val logMessages: List<LogMessage> = intent.getParcelableArrayListExtra("apduLog")!!
        val stringBuilder = StringBuilder()
        if (logMessages != null && !logMessages.isEmpty()) {
            for (logMessage in logMessages) {
                val command = "<font color=#b37700>" + logMessage.command + "</font><br>"
                val reqMessage =
                    "<font color=#77b300>" + "--&gt " + logMessage.request + "</font><br>"
                val respMessage =
                    "<font color=#00ccff>" + "&lt-- " + logMessage.response + "</font><br><br>"
                stringBuilder.append(command).append(reqMessage).append(respMessage)
            }
        }
        tvResult?.setText(Html.fromHtml(stringBuilder.toString()))
        fab.setOnClickListener(this)
        btnOk.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_ok -> finish()
            R.id.fab -> shareLog()
            else -> {
            }
        }
    }

    private fun shareLog() {
        val intent2 = Intent()
        intent2.action = Intent.ACTION_SEND
        intent2.type = "text/plain"
        intent2.putExtra(Intent.EXTRA_TEXT, tvResult!!.text.toString())
        startActivity(Intent.createChooser(intent2, "Send logs"))
    }

    companion object {
        fun newIntent(context: Context?): Intent {
            return Intent(context, ApduLogActivity::class.java)
        }
    }
}