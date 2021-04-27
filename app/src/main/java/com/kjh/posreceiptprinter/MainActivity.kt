package com.kjh.posreceiptprinter

import android.content.Context
import android.hardware.usb.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {
    private lateinit var usbInterface: UsbInterface
    private lateinit var usbEndpoint: UsbEndpoint
    private lateinit var usbConnection: UsbDeviceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager = getSystemService(Context.USB_SERVICE) as UsbManager

        val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
        val textViewInfo = findViewById<TextView>(R.id.textViewInfo)
        textViewInfo.text = device.toString()
        if (device == null) {
            val dialog = AlertDialog.Builder(this)
                    .setTitle(R.string.dialog_no_usb_title)
                    .setMessage(R.string.dialog_no_usb_message)
                    .setPositiveButton(R.string.dialog_ok) { _, _ -> finishAndRemoveTask() }
                    .setOnDismissListener { finishAndRemoveTask() }
                    .create()
            dialog.show()
            return
        }

        // TODO: Search for valid interface/endpoint instead of using index 0
        usbInterface = device.getInterface(0)
        usbEndpoint = usbInterface.getEndpoint(0)
        usbConnection = manager.openDevice(device)
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

        usbConnection.claimInterface(usbInterface, true)
        usbConnection.bulkTransfer(usbEndpoint, bytes, bytes.size, 0)
    }
}