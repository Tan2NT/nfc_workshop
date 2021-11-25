package com.tanhoang.cardreader.util.parser

import com.tanhoang.cardreader.model.Card
import java.util.regex.Pattern

object CardUtil {
    private val TRACK2_EQUIVALENT_PATTERN =
        Pattern.compile("([0-9]{1,19})D([0-9]{4})([0-9]{3})?(.*)")

    fun getCardInfoFromTrack2(track2: ByteArray): Card {
        val card = Card()
        val track2Data = HexUtil.bytesToHexadecimal(track2)
        var cardNumber = ""
        var expireDate = ""
        val service = ""
        if (track2Data != null) {
            val matcher = TRACK2_EQUIVALENT_PATTERN.matcher(track2Data)
            if (matcher.find()) {
                cardNumber = matcher.group(1)
                expireDate = matcher.group(2)
                if (expireDate != null) {
                    expireDate = expireDate.substring(2, 4) + expireDate.substring(0, 2)
                }
            }
        }
        card.track2 = track2Data
        card.pan = cardNumber
        card.expireDate = expireDate
        return card
    }
}