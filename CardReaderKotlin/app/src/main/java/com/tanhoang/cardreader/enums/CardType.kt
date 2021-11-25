package com.tanhoang.cardreader.enums

enum class CardType(val cardBrand: String) {
    MC("MasterCard"),
    VISA("Visa"),
    AMEX("AmericanExpress"),
    TROY("Troy"),
    UNKNOWN("N/A");
}
