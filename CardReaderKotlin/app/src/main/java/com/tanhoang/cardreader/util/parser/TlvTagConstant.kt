package com.tanhoang.cardreader.util.parser
object TlvTagConstant {
    // EMV Command(s) (First Bytes - Cla, Ins)
    /*'00' 'A4' SELECT*/
    val SELECT = byteArrayOf(
        0x00.toByte(),
        0xA4.toByte()
    )

    /**
     * '80' '2A' COMPUTE CRYPTOGRAPHIC CHECKSUM
     * '80' 'EA' EXCHANGE RELAY RESISTANCE DATA
     * '80' 'AE' GENERATE AC
     * '80' 'CA' GET DATA
     * '80' 'A8' GET PROCESSING OPTIONS
     * '80' 'DA' PUT DATA
     * '00' 'B2' READ RECORD
     * '80' 'D0' RECOVER AC
     */
    val COMPUTE_CRYPTOGRAPHIC_CHECKSUM = byteArrayOf(
        0x80.toByte(),
        0x2A.toByte()
    )
    val EXCHANGE_RELAY_RESISTANCE_DATA = byteArrayOf(
        0x80.toByte(),
        0xEA.toByte()
    )
    val GENERATE_AC = byteArrayOf(
        0x80.toByte(),
        0xAE.toByte()
    )
    val GET_DATA = byteArrayOf(
        0x80.toByte(),
        0xCA.toByte()
    )
    val GET_PROCESSING_OPTIONS = byteArrayOf(
        0x80.toByte(),
        0xA8.toByte()
    )
    val PUT_DATA = byteArrayOf(
        0x80.toByte(),
        0xDA.toByte()
    )
    val READ_RECORD = byteArrayOf(
        0x00.toByte(),
        0xB2.toByte()
    )
    val VERIFY = byteArrayOf(
        0x00.toByte(),
        0x20.toByte()
    )
    val RECOVER_AC = byteArrayOf(
        0x80.toByte(),
        0xD0.toByte()
    )

    // - Emv Command(s) (First Bytes - Cla, Ins)
    // TLV Tag(s) (Constant(s))
    val FCI_TLV_TAG = byteArrayOf(
        0xBF.toByte(),
        0x0C.toByte()
    ) //File Control Information (FCI) Issuer Discretionary Data
    val APP_TLV_TAG = byteArrayOf(
        0x61.toByte()
    ) // Application Template
    val AID_TLV_TAG = byteArrayOf(
        0x4F.toByte()
    ) // AID (Application Identifier)
    val APPLICATION_LABEL_TLV_TAG = byteArrayOf(
        0x50.toByte()
    ) // Application Label
    val APP_PRIORITY_IND_TLV_TAG = byteArrayOf(
        0x87.toByte()
    ) // Application Priority Indicator
    val PDOL_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x38.toByte()
    ) // PDOL (Processing Options Data Object List)
    val CDOL_1_TLV_TAG = byteArrayOf(
        0x8C.toByte()
    ) // CDOL1 (Card Risk Management Data Object List 1)
    val CDOL_2_TLV_TAG = byteArrayOf(
        0x8D.toByte()
    ) // CDOL2 (Card Risk Management Data Object List 2)
    val GPO_RMT1_TLV_TAG = byteArrayOf(
        0x80.toByte()
    ) // GPO Response message template 1
    val GPO_RMT2_TLV_TAG = byteArrayOf(
        0x77.toByte()
    ) // GPO Response message template 2
    val AFL_TLV_TAG = byteArrayOf(
        0x94.toByte()
    ) // AFL (Application File Locator)
    val LAST_ONLINE_ATC_REGISTER_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x13.toByte()
    ) // Last Online ATC (Application Transaction Counter) Register
    val PIN_TRY_COUNTER_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x17.toByte()
    ) // PIN (Personal Identification Number) Try Counter
    val TERMINAL_TYPE = byteArrayOf(
        0x9F.toByte(),
        0x35.toByte()
    ) // Terminal Type
    val ATC_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x36.toByte()
    ) // ATC (Application Transaction Counter)

    // ATC & UN
    val P_UN_ATC_TRACK1_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x63.toByte()
    )
    val N_ATC_TRACK1_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x64.toByte()
    ) // PayPass
    val P_UN_ATC_TRACK2_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x66.toByte()
    )
    val N_ATC_TRACK2_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x67.toByte()
    ) // PayPass

    // - ATC & UN
    // Log Entry
    val PAYPASS_LOG_ENTRY_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x4D.toByte()
    )
    val PAYWAVE_LOG_ENTRY_TLV_TAG = byteArrayOf(
        0xDF.toByte(),
        0x60.toByte()
    ) // PayPass, PayWave

    // - Log Entry
    // Log Format
    val PAYPASS_LOG_FORMAT_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x4F.toByte()
    )
    val PAYWAVE_LOG_FORMAT_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x80.toByte()
    ) // PayPass, PayWave

    // - Log Format
    val APPLICATION_PAN_TLV_TAG = byteArrayOf(
        0x5A.toByte()
    ) // Application PAN (Primary Account Number)
    val TRACK2_TLV_TAG = byteArrayOf(
        0x57.toByte()
    ) // Track2
    val CARDHOLDER_NAME_TLV_TAG = byteArrayOf(
        0x5F.toByte(),
        0x20.toByte()
    ) // Cardholder Name
    val APPLICATION_EXPIRATION_DATE_TLV_TAG = byteArrayOf(
        0x5F.toByte(),
        0x24.toByte()
    ) // Application Expiration Date
    val TTQ_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x66.toByte()
    ) // Terminal Transaction Qualifiers (TTQ)
    val AMOUNT_AUTHORISED_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x02.toByte()
    ) // Amount, Authorised (Numeric)
    val AMOUNT_OTHER_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x03.toByte()
    ) // Amount, Other (Numeric)
    val APPLICATION_IDENTIFIER_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x06.toByte()
    ) // Application Identifier
    val APPLICATION_VERSION_NUMBER_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x09.toByte()
    ) // Application Version Number
    val TERMINAL_COUNTRY_CODE_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x1A.toByte()
    ) // Terminal Country Code
    val TERMINAL_FLOOR_LIMIT_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x1B.toByte()
    ) // Terminal Floor Limit
    val TRANSACTION_CURRENCY_CODE_TLV_TAG = byteArrayOf(
        0x5F.toByte(),
        0x2A.toByte()
    ) // Transaction Currency Code
    val TVR_TLV_TAG = byteArrayOf(
        0x95.toByte()
    ) // Transaction Verification Results (TVR)
    val TRANSACTION_DATE_TLV_TAG = byteArrayOf(
        0x9A.toByte()
    ) // Transaction Date
    val TRANSACTION_STATUS_INFORMATION_TLV_TAG = byteArrayOf(
        0x9B.toByte()
    ) // Transaction Status Information
    val TRANSACTION_TYPE_TLV_TAG = byteArrayOf(
        0x9C.toByte()
    ) // Transaction Type
    val TRANSACTION_TIME_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x21.toByte()
    ) // Transaction Time
    val CRYPTOGRAM_INFORMATION_DATA_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x27.toByte()
    ) // Cryptogram Information Data
    val UN_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x37.toByte()
    ) // Unpredictable Number (UN)
    val MERCHANT_NAME_TLV_TAG = byteArrayOf(
        0x9F.toByte(),
        0x4E.toByte()
    ) // Merchant Name
    // - TLV Tag(s) (Constant(s))
}