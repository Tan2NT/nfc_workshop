package com.tanhoang.cardreader.util.parser

import android.util.Log
import com.tanhoang.cardreader.model.AflObject
import java.io.ByteArrayOutputStream
import java.util.ArrayList

object AflUtil {
    private val TAG = AflUtil::class.java.name
    fun getAflDataRecords(aflData: ByteArray): List<AflObject?>? {
        // Returning result
        var result: MutableList<AflObject?>? = null
        // - Returning result
        Log.d(TAG, "AFL Data Length: " + aflData.size)
        if (aflData.size < 4) { // At least 4 bytes length needed to go ahead
            try {
                throw Exception("Cannot preform AFL data byte array actions, available bytes < 4; Length is " + aflData.size)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        } else {
            result = ArrayList()
            for (i in 0 until aflData.size / 4) {
                var firstRecordNumber = aflData[4 * i + 1].toInt()
                val lastRecordNumber =
                    aflData[4 * i + 2].toInt() // First record number & final record number
                while (firstRecordNumber <= lastRecordNumber) {
                    val aflObject = AflObject(
                        sfi = aflData[4 * i].toInt() shr 3,
                        recordNumber = firstRecordNumber,
                        readCommand = null
                    )
                    var cReadRecord: ByteArray? = null
                    var readRecordByteArrayOutputStream: ByteArrayOutputStream? = null
                    try {
                        readRecordByteArrayOutputStream = ByteArrayOutputStream()
                    } catch (e: Exception) {
                        Log.e(TAG, e.toString())
                    }
                    if (readRecordByteArrayOutputStream != null) {
                        try {
                            readRecordByteArrayOutputStream.write(TlvTagConstant.READ_RECORD)
                            readRecordByteArrayOutputStream.write(
                                byteArrayOf(
                                    aflObject.recordNumber.toByte(),
                                    (aflObject.sfi shl 0x03 or 0x04).toByte()
                                )
                            )
                            readRecordByteArrayOutputStream.write(
                                byteArrayOf(
                                    0x00.toByte() // Le
                                )
                            )
                            readRecordByteArrayOutputStream.close()
                            cReadRecord = readRecordByteArrayOutputStream.toByteArray()
                        } catch (e: Exception) {
                            Log.e(TAG, e.toString())
                        }
                    }
                    if (cReadRecord != null) {
                        aflObject.readCommand = cReadRecord
                    }
                    result.add(aflObject)
                    firstRecordNumber++
                }
            }
        }
        return result
    }
}