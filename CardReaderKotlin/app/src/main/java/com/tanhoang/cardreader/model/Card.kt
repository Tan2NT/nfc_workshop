package com.tanhoang.cardreader.model

import com.tanhoang.cardreader.enums.CardType

class Card(
    var type: String = "Visa Card",
    var pan: String? = null,
    var expireDate: String? = null,
    var cardType: CardType? = null,
    var track2: String? = null,
    val emvData: String? = null,
)