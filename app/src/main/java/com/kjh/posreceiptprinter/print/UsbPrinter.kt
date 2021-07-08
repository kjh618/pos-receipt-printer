package com.kjh.posreceiptprinter.print

import android.hardware.usb.*
import android.util.Log

class UsbPrinter(
    manager: UsbManager,
    val device: UsbDevice,
) : Printer {
    // TODO: Search for valid interface/endpoint instead of using index 0
    private val interf: UsbInterface = device.getInterface(0)
    private val endpoint: UsbEndpoint = interf.getEndpoint(0)
    private val connection: UsbDeviceConnection = manager.openDevice(device)
    init {
        connection.claimInterface(interf, true)
        Log.i(this::class.simpleName, "Initialized: $this")
    }

    override fun toString(): String {
        return "UsbPrinter { device = $device, ... }"
    }

    override fun print(bytes: ByteArray): Boolean {
        Log.i(this::class.simpleName, "Printing ${bytes.size} bytes...")

        val n = connection.bulkTransfer(endpoint, bytes, bytes.size, 0)
        if (n < 0) {
            Log.w(this::class.simpleName, "Print failed: $n")
            return false
        }

        Log.i(this::class.simpleName, "Printed $n bytes")
        return true
    }

    override fun close() {
        connection.releaseInterface(interf)
        connection.close()

        Log.i(this::class.simpleName, "Closed")
    }
}