package com.kjh.posreceiptprinter.printing

const val CPL_FONT_A: Int = 42 // Characters per line

val TEST_CONTENT: PrintContent = PrintContent().apply {
    addCommand(PrinterCommand.Initialize)

    addText("프린터 테스트 (Printer Test)\n\n")

    addLine('=')
    addText("\n")

    // Test PrintModes
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
    addLine('-')
    addText("\n")

    // Test Justification
    addCommand(PrinterCommand.SelectJustification(Justification.Left))
    addText("왼쪽 정렬 (Left Justification)\n\n")
    addCommand(PrinterCommand.SelectJustification(Justification.Center))
    addText("가운데 정렬 (Center Justification)\n\n")
    addCommand(PrinterCommand.SelectJustification(Justification.Right))
    addText("오른쪽 정렬 (Right Justification)\n\n")

    addCommand(PrinterCommand.SelectPrintModes(PrintModes()))
    addLine('-')
    addText("\n")

    // Test table
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
    addText("\n")

    addLine('-')
    addText("\n")

    // Test table with long cells
    addTableRow(listOf(
        TableCell("1열", Justification.Center, 2),
        TableCell("2열", Justification.Center, 2),
        TableCell("3열", Justification.Center, 1),
        TableCell("4열", Justification.Center, 2),
    ))
    addTableRow(listOf(
        TableCell("내용이 아주 긴 칸 1-1 (A very long cell 1-1)", Justification.Center, 2),
        TableCell("내용이 아주 긴 칸 1-2 (A very long cell 1-2)", Justification.Center, 2),
        TableCell("내용이 아주 긴 칸 1-3 (A very long cell 1-3)", Justification.Center, 1),
        TableCell("내용이 아주 긴 칸 1-4 (A very long cell 1-4)", Justification.Center, 2),
    ))
    addTableRow(listOf(
        TableCell("내용이 아주 긴 칸 2-1 (A very long cell 2-1)", Justification.Center, 2),
        TableCell("내용이 아주 긴 칸 2-2 (A very long cell 2-2)", Justification.Center, 2),
        TableCell("내용이 아주 긴 칸 2-3 (A very long cell 2-3)", Justification.Center, 1),
        TableCell("내용이 아주 긴 칸 2-4 (A very long cell 2-4)", Justification.Center, 2),
    ))

    addCommand(PrinterCommand.PartialCut(100))
}

class TableCell(
    var text: String,
    private val justification: Justification,
    val weight: Int,
) {
    fun takeWidth(width: Int): String {
        var textWidth = 0

        for ((index, char) in text.withIndex()) {
            textWidth += if (char.code in 0..255) 1 else 2

            if (textWidth > width) {
                val textBeforeIndex = text.substring(0, index)
                text = text.substring(index) // iterator invalidation?
                return textBeforeIndex
            }
        }

        // textWidth <= width
        val padding = width - textWidth
        val result = when (justification) {
            Justification.Left -> text + " ".repeat(padding)
            Justification.Center -> " ".repeat(padding / 2) + text + " ".repeat((padding + 1) / 2)
            Justification.Right -> " ".repeat(padding) + text
        }
        text = ""
        return result
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
    fun addLine(char: Char) {
        addText(char.toString().repeat(CPL_FONT_A) + "\n")
    }

    // Assume font A and width 1
    fun addTableRow(cells: List<TableCell>) {
        val sumWeight = cells.sumOf { it.weight }

        val row = mutableListOf<String>()
        var isTextRemaining = true
        while (isTextRemaining) {
            isTextRemaining = false
            val rowLine = cells.joinToString(separator = "") {
                val result = it.takeWidth(CPL_FONT_A * it.weight / sumWeight)
                if (it.text.isNotEmpty()) {
                    isTextRemaining = true
                }
                result
            }
            row.add(rowLine)
        }

        row.joinToString(separator = "\n", postfix = "\n").toByteArray(CHARSET).toCollection(bytes)
    }

    fun toByteArray(): ByteArray {
        return bytes.toByteArray()
    }
}