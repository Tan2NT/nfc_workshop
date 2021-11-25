package com.tanhoang.cardreader.service

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import com.tanhoang.cardreader.util.APDUUtil
import com.tanhoang.cardreader.util.PreferenceProvider
import com.tanhoang.cardreader.util.TypeConvertor

class HostCardEmulatorService: HostApduService() {

    private lateinit var mPreferenceProvider: PreferenceProvider

    override fun onCreate() {
        super.onCreate()
        mPreferenceProvider = PreferenceProvider(applicationContext)
    }

    override fun processCommandApdu(commandApdu: ByteArray?, p1: Bundle?): ByteArray {
        if (commandApdu == null) {
            Log.d(TAG, "HostCardEmulatorService processCommandApdu command is NULL")
            return TypeConvertor.hexStringToByteArray(STATUS_FAILED)
        }

        val commandApduStr = TypeConvertor.toHexString(commandApdu)

        Log.d(TAG, "HostCardEmulatorService processCommandApdu: ${TypeConvertor.toHexString(commandApdu)}")

        val apduKey = APDUUtil.findAPDUKey(commandApduStr)
        apduKey?.let { key ->
            val apduResponseStr = mPreferenceProvider.getString(key, "")
            Log.d(TAG, "HostCardEmulatorService apduResponseStr: ${apduResponseStr}")
            return TypeConvertor.hexStringToByteArray(apduResponseStr)
        }

        return TypeConvertor.hexStringToByteArray(STATUS_FAILED)
    }


    override fun onDeactivated(reason: Int) {
        Log.d(TAG, "HostCardEmulatorService Deactivated: " + reason)
    }

    companion object {
        val TAG = "TDebug"
        val STATUS_FAILED = "6F00"
    }
}