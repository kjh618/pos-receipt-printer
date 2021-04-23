package com.kjh.posreceiptprinter

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val manager = getSystemService(Context.USB_SERVICE) as UsbManager

        val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)
        val infoText = findViewById<TextView>(R.id.textInfo)
        infoText.text = device.toString()

        val bytes = "test\n".toByteArray()

        val intf = device?.getInterface(0)
        val endpoint = intf?.getEndpoint(0)
        val connection = manager.openDevice(device)
        connection.claimInterface(intf, true)
        connection.bulkTransfer(endpoint, bytes, bytes.size, 0)
    }
}