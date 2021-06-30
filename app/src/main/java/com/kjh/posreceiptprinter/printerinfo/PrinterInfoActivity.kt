package com.kjh.posreceiptprinter.printerinfo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.kjh.posreceiptprinter.databinding.ActivityPrinterInfoBinding
import com.kjh.posreceiptprinter.print.CHARSET
import com.kjh.posreceiptprinter.print.PrinterManager

class PrinterInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrinterInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarPrinterInfo)

        binding.textViewPrinterInfo.text = PrinterManager.printer?.toString() ?: "(no printer)"
    }

    fun onClickButtonPrint(@Suppress("UNUSED_PARAMETER") view: View) {
        PrinterManager.printAndDo({ parseHexEscapes(binding.editTextPrintRaw.text.toString()) })
    }

    private fun parseHexEscapes(stringWithHex: String): ByteArray {
        val hexEscape = Regex("""\\x[0-9a-fA-F]{2}""")

        val newString = hexEscape.replace(stringWithHex) { match ->
            match.value.substring(2).toInt(16).toChar().toString()
        }
        return newString.toByteArray(CHARSET)
    }
}