package com.tanhoang.emvreadernfckotlin.lib.model

/**
 * @author AliMertOzdemir
 * @class Application
 * @created 21.04.2020
 */
class Application (
    var aid: ByteArray?,
    var appLabel: String? = null,
    var priority :Int = 0
)