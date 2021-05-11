package com.kjh.posreceiptprinter.printing

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log

object PrintManager {
    private lateinit var printer: UsbPrinter
    val isPrinterInitialized: Boolean
        get() = PrintManager::printer.isInitialized

    val content: MutableList<Byte> = mutableListOf()

    fun initializeUsbPrinter(manager: UsbManager, device: UsbDevice) {
        printer = UsbPrinter(manager, device)
        Log.i(this::class.simpleName, "USB printer initialized")
    }

    fun printBytes(bytes: ByteArray) {
        printer.print(bytes)
    }

    fun printContent() {
        printer.print(content.toByteArray())
        content.clear()
    }
}