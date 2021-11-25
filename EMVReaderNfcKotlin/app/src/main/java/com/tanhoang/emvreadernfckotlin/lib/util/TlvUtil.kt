package com.tanhoang.emvreadernfckotlin.lib.util

import android.util.Log
import com.tanhoang.emvreadernfckotlin.lib.model.Application
import java.io.ByteArrayInputStream
import java.util.*

/**
 * @author AliMertOzdemir
 * @class TlvUtil
 * @created 16.04.2020
 */
object TlvUtil {
    private val TAG = TlvUtil::class.java.name
    fun getTlvByTag(dataBytes: ByteArray, tlvTag: ByteArray): ByteArray? {
        // Returning result
        var result: ByteArray? = null
        // - Returning result
        var byteArrayInputStream: ByteArrayInputStream? = null
        try {
            byteArrayInputStream = ByteArrayInputStream(dataBytes)
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
        if (byteArrayInputStream != null) {
            if (byteArrayInputStream.available() < 2) {
                try {
                    throw Exception("Cannot preform TLV byte array stream actions, available bytes < 2; Length is " + byteArrayInputStream.available())
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
            } else {
                var i = 0
                var resultSize: Int
                var tlvTagLength: ByteArray? = ByteArray(tlvTag.size)
                while (byteArrayInputStream.read() != -1) {
                    i += 1
                    if (i >= tlvTag.size) {
                        try {
                            tlvTagLength = Arrays.copyOfRange(dataBytes, i - tlvTag.size, i)
                        } catch (e: Exception) {
                            Log.e(TAG, e.toString())
                        }
                    }
                    if (Arrays.equals(tlvTag, tlvTagLength)) {
                        resultSize = byteArrayInputStream.read()
                        if (resultSize > byteArrayInputStream.available()) {
                            continue
                        }
                        if (resultSize != -1) {
                            val resultRes = ByteArray(resultSize)
                            if (byteArrayInputStream.read(resultRes, 0, resultSize) != 0) {
                                val checkResponse = HexUtil.bytesToHexadecimal(dataBytes)
                                val checkResult = HexUtil.bytesToHexadecimal(resultRes)
                                if (checkResponse != null && checkResult != null && checkResponse.contains(
                                        checkResult
                                    )
                                ) {
                                    result = resultRes
                                }
                            }
                        }
                    }
                }
            }
            try {
                byteArrayInputStream.close()
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
        return result
    }

    fun getApplicationList(data: ByteArray?): List<Application> {
        var data = data
        val appList: MutableList<Application> = ArrayList()
        data = getTlvByTag(data!!, TlvTagConstant.FCI_TLV_TAG)
        for (app in getMultipleTlv(data, TlvTagConstant.APP_TLV_TAG)) {
            val aid = getTlvByTag(app, TlvTagConstant.AID_TLV_TAG)
            val appLabel = getTlvByTag(app, TlvTagConstant.APPLICATION_LABEL_TLV_TAG)
            val appPriorityInd = getTlvByTag(app, TlvTagConstant.APP_PRIORITY_IND_TLV_TAG)
            var priority = 0
            if (appPriorityInd != null) {
                val appPriorityIndStr = HexUtil.bytesToHexadecimal(appPriorityInd)
                if (appPriorityIndStr != null) priority = appPriorityIndStr.toInt()
            }
            val application = Application(
                aid = aid,
                appLabel = if(appLabel != null) HexUtil.bytesToAscii(appLabel) else null,
                priority = priority
            )
            appList.add(application)
        }
        return appList
    }

    private fun getMultipleTlv(data: ByteArray?, tlvTag: ByteArray?): List<ByteArray> {
        var data = data!!
        val appList: MutableList<ByteArray> = ArrayList()
        var startIndex = 0
        val endIndex = data.size
        while (endIndex > startIndex) {
            data = Arrays.copyOfRange(data, startIndex, data.size)
            val tlvData = getTlvByTag(data, tlvTag!!)
            startIndex += if (tlvData != null) {
                appList.add(tlvData)
                tlvData.size + 1 + tlvTag.size
            } else {
                return appList
            }
        }
        return appList
    } // List of fields that should be send inside Bit 55 on request
    // 5F2A - Transaction Currency Code - Indicates the currency code of the transaction according to ISO 4217
    // 82   - Application Interchange Profile - Indicates the capabilities of the card to support specific functions in the application
    // 84   - Dedicated File (DF) Name
    // 95   - Terminal Verification Results (TVR) - Status of the different functions as seen from the terminal
    // 9A   - Transaction Date - Local date that the transaction was authorised
    // 9C   - Transaction Type - Indicates the type of financial transaction, represented by the first two digits of ISO 8583:1987 Processing Code
    // 9F02 - Amount, Authorised (Numeric) - Authorised amount of the transaction (excluding adjustments)
    // 9F03 - Amount, Other (Numeric) - Secondary amount associated with the transaction representing a cashback amount
    // 9F09 - Application Version Number
    // 9F10 - Issuer Application Data - Contains proprietary application data for transmission to the issuer in an online transaction
    // 9F1A - Terminal Country Code - Indicates the country of the terminal, represented according to ISO 3166
    // 9F1E - Interface Device (IFD) Serial Number
    // 9F26 - Application Cryptogram - Cryptogram returned by the ICC in response of the GENERATE AC command
    // 9F27 - Cryptogram Information Data - Indicates the type of cryptogram and the actions to be performed by the terminal
    // 9F33 - Terminal Capabilities
    // 9F34 - Cardholder Verification Method (CVM) Results
    // 9F35 - Terminal Type
    // 9F36 - Application Transaction Counter (ATC) - Counter maintained by the application in the ICC (incrementing the ATC is managed by the ICC)
    // 9F37 - Unpredictable Number - Value to provide variability and uniqueness to the generation of a cryptogram
    // 9F41 - Transaction Sequence Counter
    // 5F34 - Application PAN Sequence Number - Identifies and differentiates cards with the same PAN
    // READ ON MASTER CARD :
    // 5F2A - // Hardcoded
    // 82   - 2000
    // 84   - 325041592E5359532E4444463031
    // 95   -
    // 9A   - // Hardcoded
    // 9C   - // Hardcoded
    // 9F02 - // From Amount Left padding 12
    // 9F03 - // Hardcoded
    // 9F09 - // Hardcoded new byte[]{0x00, 0x01});
    // 9F10 - 06011103A000000F030000002500000000250003AFC776
    // 9F1A - // Hardcoded new byte[]{0x07, (byte) 0x92}
    // 9F1E - // mDataManager.getTerminalInfo().getTerminalId().getBytes()
    // 9F26 - B4DD72A5956C1131
    // 9F27 - 80
    // 9F33 - // mTerminal.getTerminalCapability().getBytes() - 000808
    // 9F34 - 00
    // 9F35 - // Hardcoded - new byte[]{0x21}
    // 9F36 - 07D7
    // 9F37 - // Tdol une setlenmeli Ddol unden alınmalı
    // 9F41 - // Stan or 00 00 00 01 - new byte[]{0x00, 0x00, 0x00, (byte) 0x01}
    // 5F34 - // From Card 00
    // 5A - 4824914751960012
    // 57 - 4824914751960012D21042010000003202900F
}