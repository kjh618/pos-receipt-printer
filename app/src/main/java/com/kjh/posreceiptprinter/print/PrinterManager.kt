package com.kjh.posreceiptprinter.print

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import android.widget.Toast
import com.kjh.posreceiptprinter.R
import java.nio.charset.Charset

val CHARSET: Charset = Charset.forName("EUC-KR")

object PrinterManager {
    private lateinit var toastPrinterConnected: Toast
    private lateinit var toastPrinterDisconnected: Toast
    private lateinit var toastPrinting: Toast
    private lateinit var toastPrintFailed: Toast
    private lateinit var toastNoPrinter: Toast

    var printer: Printer? = null
        set(value) {
            field?.close()
            field = value
            if (value == null) {
                toastPrinterDisconnected.show()
            } else {
                toastPrinterConnected.show()
            }
        }

    var isInitialized: Boolean = false
        private set

    fun initialize(applicationContext: Context) {
        toastPrinterConnected =
            Toast.makeText(applicationContext, R.string.toast_printer_connected, Toast.LENGTH_SHORT)
        toastPrinterDisconnected =
            Toast.makeText(applicationContext, R.string.toast_printer_disconnected, Toast.LENGTH_SHORT)
        toastPrinting =
            Toast.makeText(applicationContext, R.string.toast_printing, Toast.LENGTH_SHORT)
        toastPrintFailed =
            Toast.makeText(applicationContext, R.string.toast_print_failed, Toast.LENGTH_SHORT)
        toastNoPrinter =
            Toast.makeText(applicationContext, R.string.toast_no_printer, Toast.LENGTH_SHORT)

        val usbDeviceDetachedReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == UsbManager.ACTION_USB_DEVICE_DETACHED) {
                    val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)!!
                    if (device == (printer as? UsbPrinter)?.device) {
                        printer = null
                    }
                }
            }
        }
        applicationContext.registerReceiver(
            usbDeviceDetachedReceiver,
            IntentFilter(UsbManager.ACTION_USB_DEVICE_DETACHED),
        )

        isInitialized = true

        Log.i(this::class.simpleName, "Initialized")
    }

    fun printAndDoIfSuccessful(getBytes: () -> ByteArray, doIfSuccessful: (() -> Unit)? = null) {
        if (printer != null) {
            val bytes = getBytes()

            Log.i(this::class.simpleName, "Printing...")
            toastPrinting.show()

            if (printer!!.print(bytes)) {
                Log.i(this::class.simpleName, "Print successful")

                doIfSuccessful?.invoke()
            } else {
                toastPrintFailed.show()
                Log.i(this::class.simpleName, "Print failed")

                printer = null
            }
        } else {
            toastNoPrinter.show()
            Log.i(this::class.simpleName, "No printer")
        }
    }
}