package com.tanhoang.emvreadernfckotlin.lib.model

import com.tanhoang.emvreadernfckotlin.lib.enums.CardType

/**
 * @author AliMertOzdemir
 * @class Card
 * @created 21.04.2020
 */
class Card (
    var pan: String? = null,
    var expireDate: String? = null,
    var cardType: CardType? = null,
    var track2: String? = null,
    val emvData: String? = null,
    var logMessages: List<LogMessage>? = null
)