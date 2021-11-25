package com.tanhoang.cardreader.util.parser

import android.nfc.tech.IsoDep
import android.util.Log
import com.tanhoang.cardreader.model.Card
import java.util.*

class ResponseUtils {
    companion object {

        const val TAG = "ResponseUtils"

        fun getAidFromMultiApplicationCard(responseData: ByteArray?): ByteArray? {
            var aid: ByteArray? = null
            // *** FIND MULTIPLE APPLICATIONS ***//
            val appList = TlvUtil.getApplicationList(responseData)
            aid = appList[0].aid
            return aid
        }

        fun parseProcessingOptionResponse(isoDep: IsoDep, responseData: ByteArray): Card? {
            val tag80 = TlvUtil.getTlvByTag(responseData, TlvTagConstant.GPO_RMT1_TLV_TAG)
            val tag77 = TlvUtil.getTlvByTag(responseData, TlvTagConstant.GPO_RMT2_TLV_TAG)
            if (tag77 != null) {
                return extractTrack2Data(tag77)
            }
            return null
        }

        private fun extractTrack2Data(responseData: ByteArray?): Card? {
            var card: Card? = null
            val track2 = TlvUtil.getTlvByTag(responseData!!, TlvTagConstant.TRACK2_TLV_TAG)
            val pan = TlvUtil.getTlvByTag(responseData, TlvTagConstant.APPLICATION_PAN_TLV_TAG)
            if (track2 != null && card?.track2 == null) {
                card = CardUtil.getCardInfoFromTrack2(track2)
            }
            if (pan != null && card?.pan == null) {
                card?.pan = (HexUtil.bytesToHexadecimal(pan))
            }
            return card
        }
    }
}