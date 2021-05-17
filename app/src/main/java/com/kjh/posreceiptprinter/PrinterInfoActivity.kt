package com.kjh.posreceiptprinter

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kjh.posreceiptprinter.databinding.ActivityPrinterInfoBinding
import com.kjh.posreceiptprinter.printing.CHARSET
import com.kjh.posreceiptprinter.printing.PrintManager

class PrinterInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrinterInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrinterInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarPrinterInfo)

        binding.textViewPrinterInfo.text = if (PrintManager.isPrinterInitialized) {
            PrintManager.printer.toString()
        } else {
            "(no printer)"
        }
    }

    fun onClickButtonPrint(@Suppress("UNUSED_PARAMETER") view: View) {
        if (PrintManager.isPrinterInitialized) {
            Toast.makeText(applicationContext, R.string.toast_printing, Toast.LENGTH_SHORT).show()
            val bytes = parseHexEscapes(binding.editTextPrintRaw.text.toString())
            PrintManager.printer.print(bytes)
        } else {
            Toast.makeText(applicationContext, R.string.toast_no_printer, Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseHexEscapes(stringWithHex: String): ByteArray {
        val hexEscape = Regex("""\\x[0-9a-fA-F]{2}""")

        val newString = hexEscape.replace(stringWithHex) { match ->
            match.value.substring(2).toInt(16).toChar().toString()
        }
        return newString.toByteArray(CHARSET)
    }
}