package com.kjh.posreceiptprinter

import android.content.Context
import android.hardware.usb.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    lateinit var usbInterface: UsbInterface
    lateinit var usbEndpoint: UsbEndpoint
    lateinit var usbConnection: UsbDeviceConnection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager = getSystemService(Context.USB_SERVICE) as UsbManager

        val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
        val infoText = findViewById<TextView>(R.id.textInfo)
        infoText.text = device.toString()
        if (device == null) {
            finish()
            return
        }

        usbInterface = device.getInterface(0)
        usbEndpoint = usbInterface.getEndpoint(0)
        usbConnection = manager.openDevice(device)
    }

    fun print(view: View) {
        val editTextContent = findViewById<EditText>(R.id.editTextPrintContent)
        val bytes = editTextContent.text.toString().toByteArray()
        usbConnection.claimInterface(usbInterface, true)
        usbConnection.bulkTransfer(usbEndpoint, bytes, bytes.size, 0)
    }
}