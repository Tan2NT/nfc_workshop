package com.tanhoang.cardreader.util

import android.nfc.tech.IsoDep

class APDUUtil {
    companion object {
        fun executeApduCommand(apduCommandString: String, isoDep: IsoDep): ByteArray {
            return isoDep.transceive(TypeConvertor.hexStringToByteArray(apduCommandString))
        }

        fun findAPDUKey(apduCommandString: String): String? {
            for ((key, command) in apduCommandMap) {
                if(command.equals(apduCommandString, true))
                    return key
            }
            return null
        }

        val apduCommandMap: Map<String, String> = mapOf(
            "SELECT_AID" to "00A4040007A000000003101000",
            "GET_PROCESSING_OPTIONAL" to "80A8000023832127800000000000000001000000000000079200000000000940211005007AD0011C00",
            "GET_PIN_TRY_COUNTER" to "80CA9F1700",
            "GET_TRANSACTION_COUNTER" to "80CA9F3600"
        )
    }
}