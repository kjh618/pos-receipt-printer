package com.kjh.posreceiptprinter.print

import android.hardware.usb.*

class UsbPrinter(
    manager: UsbManager,
    val device: UsbDevice,
) {
    // TODO: Search for valid interface/endpoint instead of using index 0
    private val interf: UsbInterface = device.getInterface(0)
    private val endpoint: UsbEndpoint = interf.getEndpoint(0)
    private val connection: UsbDeviceConnection = manager.openDevice(device)
    init {
        connection.claimInterface(interf, true)
    }

    override fun toString(): String {
        return "UsbPrinter(device=$device, ...)"
    }

    fun print(bytes: ByteArray) {
        connection.bulkTransfer(endpoint, bytes, bytes.size, 0)
    }

    fun close() {
        connection.releaseInterface(interf)
        connection.close()
    }
}