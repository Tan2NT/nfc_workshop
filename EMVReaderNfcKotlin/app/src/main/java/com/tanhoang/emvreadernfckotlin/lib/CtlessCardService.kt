package com.tanhoang.emvreadernfckotlin.lib

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
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.CountDownTimer
import android.widget.Toast
import android.nfc.TagLostException
import android.util.Log
import com.tanhoang.emvreadernfckotlin.lib.exception.CommandException
import com.tanhoang.emvreadernfckotlin.lib.model.*
import java.io.IOException
import java.lang.Exception
import java.util.*

/**
 * @author AliMertOzdemir
 * @class CtlessCardService
 * @created 20.04.2020
 */
class CtlessCardService : ReaderCallback {
    private var mContext: Activity? = null
    private var mNfcAdapter: NfcAdapter? = null
    private var mIsoDep: IsoDep? = null
    private var mResultListener: ResultListener? = null
    private val READ_TIMEOUT = 3000
    private var CONNECT_TIMEOUT = 30000
    private val mTimer: CountDownTimer? = null
    private var mLogMessages: MutableList<LogMessage>? = null
    private var mUserAppIndex = -1
    private var mCard: Card? = null

    private constructor() {}
    constructor(context: Activity?, resultListener: ResultListener?) {
        mContext = context
        mResultListener = resultListener
    }

    fun start() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(mContext)
        // Check if the device has NFC
        if (mNfcAdapter == null) {
            Toast.makeText(mContext, "NFC not supported", Toast.LENGTH_LONG).show()
        }
        // Check if NFC is enabled on device
        mNfcAdapter?.let { nfcAdapter ->
            if(!nfcAdapter.isEnabled) {
                Toast.makeText(
                    mContext, "Enable NFC before using the app",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                nfcAdapter.enableReaderMode(mContext, this, READER_FLAGS, null)
            }
        }
    }

    fun setTimeout(timeoutMillis: Int) {
        CONNECT_TIMEOUT = timeoutMillis
    }

    fun setSelectedApplication(index: Int) {
        mUserAppIndex = index
    }

    override fun onTagDiscovered(tag: Tag) {
        mLogMessages = ArrayList()
        mCard = Card()
        val tagList = tag.techList
        for (tagName in tagList) {
            Log.d(TAG, "TAG NAME : $tagName")
        }
        mIsoDep = IsoDep.get(tag)
        if (mIsoDep != null && mIsoDep!!.tag != null) {
            Log.d(TAG, "ISO-DEP - Compatible NFC tag discovered: " + mIsoDep!!.tag)
            mResultListener!!.onCardDetect()
            try {
                mIsoDep!!.connect()
                mIsoDep!!.timeout = READ_TIMEOUT
                var command = ApduUtil.selectPpse()
                var result = mIsoDep!!.transceive(command)
                var responseData = evaluateResult("SELECT PPSE", command, result)
                var aid = TlvUtil.getTlvByTag(responseData!!, TlvTagConstant.AID_TLV_TAG)
                aid = getAidFromMultiApplicationCard(responseData)
                if (aid == null) return
                if (!AidUtil.isApprovedAID(aid)) {
                    throw CommandException("AID NOT SUPPORTED -> " + HexUtil.bytesToHexadecimal(aid), null)
                }
                command = ApduUtil.selectApplication(aid)
                result = mIsoDep!!.transceive(command)
                responseData = evaluateResult("SELECT APPLICATION", command, result)
                var pdol = TlvUtil.getTlvByTag(responseData!!, TlvTagConstant.PDOL_TLV_TAG)
                pdol = GpoUtil.generatePdol(pdol)
                command = ApduUtil.getProcessingOption(pdol!!)
                result = mIsoDep!!.transceive(command)
                responseData = evaluateResult("GET PROCESSING OPTION", command, result)
                val tag80 = TlvUtil.getTlvByTag(responseData!!, TlvTagConstant.GPO_RMT1_TLV_TAG)
                val tag77 = TlvUtil.getTlvByTag(responseData, TlvTagConstant.GPO_RMT2_TLV_TAG)
                var aflData: ByteArray? = null
                if (tag77 != null) {
                    extractTrack2Data(tag77)
                    aflData = TlvUtil.getTlvByTag(responseData, TlvTagConstant.AFL_TLV_TAG)
                } else if (tag80 != null) {
                    aflData = tag80
                    aflData = Arrays.copyOfRange(aflData, 2, aflData.size)
                }
                if (aflData != null) {
                    Log.d(TAG, "AFL HEX DATA -> " + HexUtil.bytesToHexadecimal(aflData))
                    val aflDatas = AflUtil.getAflDataRecords(aflData)
                    if (aflDatas != null && !aflDatas.isEmpty() && aflDatas.size < 10) {
                        Log.d(TAG, "AFL DATA SIZE -> " + aflDatas.size)
                        for (aflObject in aflDatas) {
                            command = aflObject?.readCommand
                            result = mIsoDep!!.transceive(command)
                            responseData = evaluateResult(
                                "READ RECORD (sfi: " + aflObject?.sfi + " record: " + aflObject?.recordNumber + ")",
                                command,
                                result
                            )
                            extractTrack2Data(responseData)
                        }
                    }
                } else {
                    Log.d(TAG, "AFL HEX DATA -> NULL")
                }
                if (mCard?.track2 != null) {
                    mCard?.cardType = AidUtil.getCardBrandByAID(aid)
                } else {
                    throw Exception("CALL YOUR BANK")
                }
                command = ApduUtil.getReadTlvData(TlvTagConstant.PIN_TRY_COUNTER_TLV_TAG)
                result = mIsoDep!!.transceive(command)
                evaluateResult("PIN TRY COUNT", command, result)
                command = ApduUtil.getReadTlvData(TlvTagConstant.ATC_TLV_TAG)
                result = mIsoDep!!.transceive(command)
                evaluateResult("APPLICATION TRANSACTION COUNTER", command, result, true)
            } catch (e: CommandException) {
                Log.d(TAG, "COMMAND EXCEPTION -> " + e.localizedMessage)
                mResultListener!!.onCardReadFail("COMMAND EXCEPTION -> " + e.localizedMessage)
            } catch (e: TagLostException) {
                Log.d(TAG, "ISO DEP TAG LOST ERROR -> " + e.localizedMessage)
                mResultListener!!.onCardMovedSoFast()
            } catch (e: IOException) {
                Log.d(TAG, "ISO DEP CONNECT ERROR -> " + e.localizedMessage)
                mResultListener!!.onCardReadFail("ISO DEP CONNECT ERROR -> " + e.localizedMessage)
            } catch (e: Exception) {
                Log.d(TAG, "CARD ERROR -> " + e.localizedMessage)
                mResultListener!!.onCardReadFail("CARD ERROR -> " + e.localizedMessage)
            }
            /*finally {
                mNfcAdapter.disableReaderMode(mContext);
            }*/
        } else {
            Log.d(TAG, "ISO DEP is null")
            mResultListener!!.onCardReadFail("ISO DEP is null")
        }
    }

    @Throws(IOException::class)
    private fun evaluateResult(
        commandName: String,
        command: ByteArray?,
        result: ByteArray,
        isLastCommand: Boolean = false
    ): ByteArray? {
        val apduResponse = ApduResponse(result)
        returnMessage(
            commandName,
            HexUtil.bytesToHexadecimal(command!!),
            HexUtil.bytesToHexadecimal(result),
            isLastCommand
        )
        Log.d(
            TAG, "$commandName REQUEST : " + HexUtil.bytesToHexadecimal(
                command
            )
        )
        return if (apduResponse.isStatus(SW_NO_ERROR)) {
            Log.d(
                TAG,
                commandName + " RESULT : " + HexUtil.bytesToHexadecimal(result)
            )
            apduResponse.data
        } else {
            val error = commandName + " ERROR : " + HexUtil.bytesToHexadecimal(result)
            Log.d(TAG, "COMMAND EXCEPTION -> $error")
            apduResponse.data
        }
    }

    private fun extractTrack2Data(responseData: ByteArray?) {
        val track2 = TlvUtil.getTlvByTag(responseData!!, TlvTagConstant.TRACK2_TLV_TAG)
        val pan = TlvUtil.getTlvByTag(responseData, TlvTagConstant.APPLICATION_PAN_TLV_TAG)
        if (track2 != null && mCard?.track2 == null) {
            mCard = CardUtil.getCardInfoFromTrack2(track2)
        }
        if (pan != null && mCard?.pan == null) {
            mCard?.pan = (HexUtil.bytesToHexadecimal(pan))
        }
    }

    @Throws(IOException::class)
    private fun readExtraRecord() {
        var command = ApduUtil.getReadTlvData(TlvTagConstant.AMOUNT_AUTHORISED_TLV_TAG)
        var result = mIsoDep!!.transceive(command)
        evaluateResult("AMOUNT AUTHORIZED", command, result)
        command = ApduUtil.getReadTlvData(TlvTagConstant.AMOUNT_OTHER_TLV_TAG)
        result = mIsoDep!!.transceive(command)
        evaluateResult("AMOUNT OTHER", command, result)
        command = ApduUtil.getReadTlvData(TlvTagConstant.PAYPASS_LOG_FORMAT_TLV_TAG)
        result = mIsoDep!!.transceive(command)
        evaluateResult("PAYPASS LOG FORMAT", command, result)
        command = ApduUtil.getReadTlvData(TlvTagConstant.PAYWAVE_LOG_FORMAT_TLV_TAG)
        result = mIsoDep!!.transceive(command)
        evaluateResult("PAYWAVE LOG FORMAT", command, result)
        command = ApduUtil.verifyPin(null)
        result = mIsoDep!!.transceive(command)
        evaluateResult("VERIFY PIN", command, result)
    }

    @Throws(IOException::class)
    private fun returnMessage(
        commandName: String,
        request: String?,
        response: String?,
        isLastCommand: Boolean
    ) {
        val reqMessage = request!!.replace("..".toRegex(), "$0 ")
        val respMessage = response!!.replace("..".toRegex(), "$0 ")
        val logMessage = LogMessage(commandName, reqMessage, respMessage)
        mLogMessages!!.add(logMessage)
        if (isLastCommand) {
            mCard?.logMessages = mLogMessages
            mResultListener!!.onCardReadSuccess(mCard)
            mIsoDep!!.close()
            mNfcAdapter!!.disableReaderMode(mContext)
        }
    }

    private fun getAidFromMultiApplicationCard(responseData: ByteArray?): ByteArray? {
        var aid: ByteArray? = null
        // *** FIND MULTIPLE APPLICATIONS ***//
        val appList = TlvUtil.getApplicationList(responseData)
        if (appList.size > 1) {
            if (mUserAppIndex != -1 && appList.size > mUserAppIndex) {
                aid = appList[mUserAppIndex].aid
                mUserAppIndex = -1
            } else {
                mResultListener!!.onCardSelectApplication(appList)
            }
        } else {
            aid = appList[0].aid
        }
        return aid
    }

    interface ResultListener {
        fun onCardDetect()
        fun onCardReadSuccess(card: Card?)
        fun onCardReadFail(error: String)
        fun onCardReadTimeout()
        fun onCardMovedSoFast()
        fun onCardSelectApplication(applications: List<Application?>?)
    }

    companion object {
        private val TAG = CtlessCardService::class.java.name

        //  reader mode flags: listen for type A (not B), skipping ndef check
        private const val READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
                NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS
        private const val SW_NO_ERROR = 0x9000
    }
}