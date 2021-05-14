package com.kjh.posreceiptprinter.printing

const val CPL_FONT_A: Int = 42 // Characters per line

private const val ESC: Char = '\u001B'
private const val GS: Char = '\u001D'

private fun Boolean.toIntBit(position: Int): Int {
    return if (this) 1 shl position else 0
}

class PrintModes(
    private val font2: Boolean = false,
    private val emphasized: Boolean = false,
    private val doubleHeight: Boolean = false,
    private val doubleWidth: Boolean = false,
    private val underline: Boolean = false,
) {
    fun toByte(): Byte {
        val n = 0 or
            font2.toIntBit(0) or
            emphasized.toIntBit(3) or
            doubleHeight.toIntBit(4) or
            doubleWidth.toIntBit(5) or
            underline.toIntBit(7)
        return n.toByte()
    }
}

enum class Justification { Left, Center, Right }

sealed class PrinterCommand(val bytes: ByteArray) {
    // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=192
    object Initialize : PrinterCommand("$ESC@".toByteArray(CHARSET))

    // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=23
    class SelectPrintModes(printModes: PrintModes) :
        PrinterCommand("$ESC!".toByteArray(CHARSET) + printModes.toByte())

    // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=58
    class SelectJustification(justification: Justification) :
        PrinterCommand("${ESC}a".toByteArray(CHARSET) + justification.ordinal.toByte())

    // https://reference.epson-biz.com/modules/ref_escpos/index.php?content_id=87
    class PartialCut(feedAmount: Byte) :
        PrinterCommand("${GS}V".toByteArray(CHARSET) + 66 + feedAmount)
}