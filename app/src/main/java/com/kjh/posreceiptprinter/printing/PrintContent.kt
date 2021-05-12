package com.kjh.posreceiptprinter.printing

class PrintContent(private var bytes: MutableList<Byte> = mutableListOf()) {
    fun addCommand(command: PrinterCommand) {
        command.bytes.toCollection(bytes)
    }

    fun addText(text: String) {
        text.toByteArray(CHARSET).toCollection(bytes)
    }

    fun toByteArray(): ByteArray {
        return bytes.toByteArray()
    }
}