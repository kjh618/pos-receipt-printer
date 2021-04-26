package com.kjh.posreceiptprinter

import android.content.Context
import android.hardware.usb.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import java.nio.charset.Charset

class MainActivity : AppCompatActivity() {
    private lateinit var usbInterface: UsbInterface
    private lateinit var usbEndpoint: UsbEndpoint
    private lateinit var usbConnection: UsbDeviceConnection

    private var printContent: MutableList<Byte> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager = getSystemService(Context.USB_SERVICE) as UsbManager

        val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
        val textViewInfo = findViewById<TextView>(R.id.textViewInfo)
        textViewInfo.text = device.toString()
        if (device == null) {
            finishAndRemoveTask()
            return
        }

        usbInterface = device.getInterface(0)
        usbEndpoint = usbInterface.getEndpoint(0)
        usbConnection = manager.openDevice(device)
    }

    private fun updateTextViewPrintContent() {
        val textViewPrintContent = findViewById<TextView>(R.id.textViewPrintContent)
        textViewPrintContent.text = printContent.toString()
    }

    fun inputNumber(view: View) {
        val editTextNumberInput = findViewById<EditText>(R.id.editTextNumberInput)
        val byte = editTextNumberInput.text.toString().toByte()
        editTextNumberInput.setText("")
        printContent.add(byte)
        updateTextViewPrintContent()
    }

    fun delete(view: View) {
        printContent.removeLastOrNull()
        updateTextViewPrintContent()
    }

    fun inputString(view: View) {
        val editTextStringInput = findViewById<EditText>(R.id.editTextStringInput)
        val bytes = editTextStringInput.text.toString().toByteArray(Charset.forName("EUC-KR"))
        bytes.toCollection(printContent)
        updateTextViewPrintContent()
    }

    fun print(view: View) {
        usbConnection.claimInterface(usbInterface, true)
        usbConnection.bulkTransfer(usbEndpoint, printContent.toByteArray(), printContent.size, 0)
    }
}