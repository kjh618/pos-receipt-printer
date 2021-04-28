package com.kjh.posreceiptprinter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import java.nio.charset.Charset

class DebugActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        setSupportActionBar(findViewById(R.id.toolbarDebug))

        val textViewInfo = findViewById<TextView>(R.id.textViewInfo)
        textViewInfo.text = MainActivity.printer.usbDeviceToString()
    }

    private fun parseHexEscapes(stringWithHex: String): ByteArray {
        val hexEscape = Regex("""\\x[0-9a-fA-F]{2}""")

        val newString = hexEscape.replace(stringWithHex) { match ->
            match.value.substring(2).toInt(16).toChar().toString()
        }
        return newString.toByteArray(Charset.forName("EUC-KR"))
    }

    fun print(view: View) {
        val editTextInput = findViewById<EditText>(R.id.editTextInput)
        val bytes = parseHexEscapes(editTextInput.text.toString())
        MainActivity.printer.print(bytes)
    }
}