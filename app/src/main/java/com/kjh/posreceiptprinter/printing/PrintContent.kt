package com.kjh.posreceiptprinter.printing

const val CPL_FONT_A: Int = 42 // Characters per line

val TEST_CONTENT: PrintContent = PrintContent().apply {
    addCommand(PrinterCommand.Initialize)

    addText("프린터 테스트 (Printer Test)\n\n")

    addText("=".repeat(CPL_FONT_A) + "\n\n")

    addCommand(PrinterCommand.SelectPrintModes(PrintModes(emphasized = true)))
    addText("강조 (Emphasized)\n\n")
    addCommand(PrinterCommand.SelectPrintModes(PrintModes(doubleHeight = true)))
    addText("높이 2배 (Double Height)\n\n")
    addCommand(PrinterCommand.SelectPrintModes(PrintModes(doubleWidth = true)))
    addText("너비 2배 (Double Width)\n\n")
    addCommand(PrinterCommand.SelectPrintModes(PrintModes(doubleHeight = true, doubleWidth = true)))
    addText("높이/너비 2배 (Double Height/Width)\n\n")
    addCommand(PrinterCommand.SelectPrintModes(PrintModes(
        emphasized = true,
        doubleHeight = true,
        doubleWidth = true,
    )))
    addText("강조 + 높이/너비 2배 (Emphasized + Double Height/Width)\n\n")

    addCommand(PrinterCommand.SelectPrintModes(PrintModes()))
    addText("-".repeat(CPL_FONT_A) + "\n\n")

    addCommand(PrinterCommand.SelectJustification(Justification.Left))
    addText("왼쪽 정렬 (Left Justification)\n\n")
    addCommand(PrinterCommand.SelectJustification(Justification.Center))
    addText("가운데 정렬 (Center Justification)\n\n")
    addCommand(PrinterCommand.SelectJustification(Justification.Right))
    addText("오른쪽 정렬 (Right Justification)\n")

    addCommand(PrinterCommand.PartialCut(100))
}

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