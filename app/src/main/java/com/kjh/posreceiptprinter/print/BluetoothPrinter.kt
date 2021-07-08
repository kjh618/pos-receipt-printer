package com.kjh.posreceiptprinter.print

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*

class BluetoothPrinter(
    private val device: BluetoothDevice,
) : Printer {
    private val socket: BluetoothSocket = device.createRfcommSocketToServiceRecord(
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // TODO: Make into a setting?
    )
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