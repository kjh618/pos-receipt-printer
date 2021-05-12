package com.kjh.posreceiptprinter.printing

import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import java.nio.charset.Charset

val CHARSET: Charset = Charset.forName("EUC-KR")

object PrintManager {
    lateinit var printer: UsbPrinter
        private set
    val isPrinterInitialized: Boolean
        get() = PrintManager::printer.isInitialized

    fun initializeUsbPrinter(manager: UsbManager, device: UsbDevice) {
        printer = UsbPrinter(manager, device)
    }
}