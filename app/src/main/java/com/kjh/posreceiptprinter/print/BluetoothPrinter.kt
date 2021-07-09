package com.kjh.posreceiptprinter.print

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*

class BluetoothPrinter(
    private val device: BluetoothDevice,
    rfcommUuid: UUID,
) : Printer {
    private val socket: BluetoothSocket =
        device.createRfcommSocketToServiceRecord(rfcommUuid)
    init {
        socket.connect()
        Log.i(this::class.simpleName, "Initialized: $this")
    }

    override fun toString(): String {
        return "BluetoothPrinter { device = $device, ... }"
    }

    override fun print(bytes: ByteArray): Boolean {
        Log.i(this::class.simpleName, "Printing ${bytes.size} bytes...")

        try {
            socket.outputStream.write(bytes)
        } catch (e: IOException) {
            Log.w(this::class.simpleName, "Print failed: $e")
            return false
        }

        Log.i(this::class.simpleName, "Printed ${bytes.size} bytes")
        return true
    }

    override fun close() {
        socket.close()

        Log.i(this::class.simpleName, "Closed")
    }
}