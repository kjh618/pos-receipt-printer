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
    addText("오른쪽 정렬 (Right Justification)\n\n")

    addCommand(PrinterCommand.SelectPrintModes(PrintModes()))
    addText("-".repeat(CPL_FONT_A) + "\n\n")

    addTableRow(listOf(
        TableCell("1열", Justification.Center, 2),
        TableCell("2열", Justification.Center, 2),
        TableCell("3열", Justification.Center, 1),
        TableCell("4열", Justification.Center, 2),
    ))
    addTableRow(listOf(
        TableCell("상품 1", Justification.Left, 2),
        TableCell("10,000", Justification.Right, 2),
        TableCell("2", Justification.Right, 1),
        TableCell("20,000", Justification.Right, 2),
    ))
    addTableRow(listOf(
        TableCell("상품 2", Justification.Left, 2),
        TableCell("30,000", Justification.Right, 2),
        TableCell("4", Justification.Right, 1),
        TableCell("120,000", Justification.Right, 2),
    ))

    addCommand(PrinterCommand.PartialCut(100))
}

data class TableCell(
    val text: String,
    val justification: Justification,
    val weight: Int,
) {
    fun fillWidth(width: Int): String {
        val textWidth = text.sumOf { if (it.code in 0..255) 1 else 2 as Int }
        val padding = width - textWidth
        // TODO: padding < 0?
        return when (justification) {
            Justification.Left -> text + " ".repeat(padding)
            Justification.Center -> " ".repeat(padding / 2) + text + " ".repeat((padding + 1) / 2)
            Justification.Right -> " ".repeat(padding) + text
        }
    }
}

class PrintContent(private var bytes: MutableList<Byte> = mutableListOf()) {
    fun addCommand(command: PrinterCommand) {
        command.bytes.toCollection(bytes)
    }

    fun addText(text: String) {
        text.toByteArray(CHARSET).toCollection(bytes)
    }

    // Assume font A and width 1
    fun addTableRow(cells: List<TableCell>) {
        val sumWeight = cells.sumOf { it.weight }
        val row = cells.joinToString(separator = "") {
            it.fillWidth(CPL_FONT_A * it.weight / sumWeight)
        } + "\n"

        row.toByteArray(CHARSET).toCollection(bytes)
    }

    fun toByteArray(): ByteArray {
        return bytes.toByteArray()
    }
}