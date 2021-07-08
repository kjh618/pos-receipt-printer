package com.kjh.posreceiptprinter.print

sealed interface Printer {
    fun print(bytes: ByteArray): Boolean

    fun close()
}