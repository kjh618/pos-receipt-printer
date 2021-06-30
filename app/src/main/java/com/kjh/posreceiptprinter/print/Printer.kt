package com.kjh.posreceiptprinter.print

interface Printer {
    fun print(bytes: ByteArray): Boolean

    fun close()
}