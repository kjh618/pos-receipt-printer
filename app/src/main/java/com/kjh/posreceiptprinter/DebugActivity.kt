package com.kjh.posreceiptprinter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.kjh.posreceiptprinter.databinding.ActivityDebugBinding
import java.nio.charset.Charset

class DebugActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDebugBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDebugBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarDebug)

        binding.textViewPrinterInfo.text = Printer.toString()
    }

    private fun parseHexEscapes(stringWithHex: String): ByteArray {
        val hexEscape = Regex("""\\x[0-9a-fA-F]{2}""")

        val newString = hexEscape.replace(stringWithHex) { match ->
            match.value.substring(2).toInt(16).toChar().toString()
        }
        return newString.toByteArray(Charset.forName("EUC-KR"))
    }

    fun print(view: View) {
        if (Printer.isInitialized) {
            val bytes = parseHexEscapes(binding.editTextPrintContent.text.toString())
            Printer.print(bytes)
        } else {
            Toast.makeText(applicationContext, R.string.toast_no_printer, Toast.LENGTH_SHORT).show()
        }
    }
}