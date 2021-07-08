package com.kjh.posreceiptprinter.print

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.util.Log
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.kjh.posreceiptprinter.R
import java.nio.charset.Charset

val CHARSET: Charset = Charset.forName("EUC-KR")

object PrinterManager {
    var printer: Printer? = null
        set(value) {
            field?.close()
            field = value
            Log.i(this::class.simpleName, "Printer set to: $value")
        }

    var isInitialized: Boolean = false
        private set

    fun initialize() {
        isInitialized = true
        Log.i(this::class.simpleName, "Initialized")
    }

    fun printAndDoIfSuccessful(
        snackbarView: View,
        getBytes: () -> ByteArray,
        doIfSuccessful: (() -> Unit)? = null,
    ) {
        if (printer != null) {
            val bytes = getBytes()

            Snackbar.make(snackbarView, R.string.printing, Snackbar.LENGTH_SHORT).show()
            Log.i(this::class.simpleName, "Printing...")

            if (printer!!.print(bytes)) {
                Log.i(this::class.simpleName, "Print successful")

                doIfSuccessful?.invoke()
            } else {
                Snackbar.make(snackbarView, R.string.print_failed, Snackbar.LENGTH_SHORT).show()
                Log.i(this::class.simpleName, "Print failed")
            }
        } else {
            Snackbar.make(snackbarView, R.string.printer_not_connected, Snackbar.LENGTH_SHORT)
                .show()
            Log.i(this::class.simpleName, "Printer not connected")
        }
    }
}