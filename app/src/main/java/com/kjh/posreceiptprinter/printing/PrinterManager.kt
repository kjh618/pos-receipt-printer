package com.kjh.posreceiptprinter.printing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import android.widget.Toast
import com.kjh.posreceiptprinter.R
import java.nio.charset.Charset

val CHARSET: Charset = Charset.forName("EUC-KR")

object PrinterManager {
    var printer: UsbPrinter? = null
        private set

    lateinit var toastPrinterInitialized: Toast
        private set
    lateinit var toastPrinterDetached: Toast
        private set
    lateinit var toastPrinting: Toast
        private set
    lateinit var toastNoPrinter: Toast
        private set
    var isInitialized: Boolean = false
        private set

    fun initialize(applicationContext: Context, deviceDetachedReceiver: BroadcastReceiver) {
        toastPrinterInitialized =
            Toast.makeText(applicationContext, R.string.toast_printer_initialized, Toast.LENGTH_SHORT)
        toastPrinterDetached =
            Toast.makeText(applicationContext, R.string.toast_printer_detached, Toast.LENGTH_SHORT)
        toastPrinting =
            Toast.makeText(applicationContext, R.string.toast_printing, Toast.LENGTH_SHORT)
        toastNoPrinter =
            Toast.makeText(applicationContext, R.string.toast_no_printer, Toast.LENGTH_SHORT)

        applicationContext.registerReceiver(
            deviceDetachedReceiver,
            IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED),
        )

        isInitialized = true

        Log.i(this::class.simpleName, "PrinterManager initialized")
    }

    fun initializeUsbPrinter(manager: UsbManager, device: UsbDevice) {
        printer = UsbPrinter(manager, device)

        toastPrinterInitialized.show()
        Log.i(this::class.simpleName, "USB printer initialized")
    }

    fun removeUsbPrinter() {
        printer!!.close()
        printer = null

        toastPrinterDetached.show()
        Log.i(this::class.simpleName, "USB printer detached")
    }

    fun printAndDo(getBytes: () -> ByteArray, doAfterPrint: (() -> Unit)? = null) {
        if (printer != null) {
            val bytes = getBytes()

            toastPrinting.show()
            Log.i(this::class.simpleName, "Printing ${bytes.size} bytes...")

            printer!!.print(bytes)
            doAfterPrint?.invoke()
        } else {
            toastNoPrinter.show()
        }
    }
}