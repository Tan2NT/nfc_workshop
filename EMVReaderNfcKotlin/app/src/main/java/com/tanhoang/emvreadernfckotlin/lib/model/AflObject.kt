package com.tanhoang.emvreadernfckotlin.lib.model

/**
 * @author AliMertOzdemir
 * @class AflObject
 * @created 17.04.2020
 */
class AflObject(
    val sfi : Int = 0,
    val recordNumber: Int = 0,
    var readCommand: ByteArray?
)