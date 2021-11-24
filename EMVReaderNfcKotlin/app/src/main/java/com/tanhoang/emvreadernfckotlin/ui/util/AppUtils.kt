package com.tanhoang.emvreadernfckotlin.ui.util

import android.app.ProgressDialog
import android.app.Activity
import android.app.AlertDialog
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
import com.tanhoang.emvreadernfckotlin.lib.model.ApduResponse

object AppUtils {
    fun showLoadingDialog(context: Context?, title: String?, message: String?): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setTitle(title)
        progressDialog.setMessage(message)
        progressDialog.isIndeterminate = true
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
        return progressDialog
    }

    fun showAlertDialog(
        activity: Activity?,
        title: String?,
        message: String?,
        positiveBtnText: String?,
        negativeBtnText: String?,
        isCancelable: Boolean,
        listener: DialogInterface.OnClickListener?
    ): AlertDialog {
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveBtnText, listener)
            .setCancelable(isCancelable)
        if (negativeBtnText != null && !negativeBtnText.isEmpty()) alertDialog.setNegativeButton(
            negativeBtnText,
            listener
        )
        return alertDialog.show()
    }

    fun showSnackBar(containerView: View?, message: String?, buttonText: String?): Snackbar {
        val snackbar = Snackbar.make(containerView!!, message!!, Snackbar.LENGTH_LONG)
        snackbar.setAction(buttonText) { view: View? -> snackbar.dismiss() }
        snackbar.duration = 2500
        snackbar.show()
        return snackbar
    }

    fun showSingleChoiceListDialog(
        activity: Activity?,
        title: String?,
        list: Array<String?>?,
        positiveBtnText: String?,
        listener: DialogInterface.OnClickListener?
    ): AlertDialog {
        // setup the alert builder
        val alertDialog = AlertDialog.Builder(activity)
            .setTitle(title)
            .setPositiveButton(positiveBtnText, null)
            .setCancelable(false)
        alertDialog.setSingleChoiceItems(list, -1, listener)
        return alertDialog.show()
    }
}