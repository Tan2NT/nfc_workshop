package com.tanhoang.cardreader.model

class AflObject(
    val sfi: Int = 0,
    val recordNumber: Int = 0,
    var readCommand: ByteArray?
)