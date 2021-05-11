package com.kjh.posreceiptprinter.printing

import android.hardware.usb.*

class UsbPrinter(
    manager: UsbManager,
    private val device: UsbDevice,
) {
    // TODO: Search for valid interface/endpoint instead of using index 0
    private val interf: UsbInterface = device.getInterface(0)
    private val endpoint: UsbEndpoint = interf.getEndpoint(0)
    private val connection: UsbDeviceConnection = manager.openDevice(this.device)
    init {
        connection.claimInterface(interf, true)
    }

    override fun toString(): String {
        return "UsbPrinter(usbDevice=$device, ...)"
    }

    fun print(bytes: ByteArray) {
        connection.bulkTransfer(endpoint, bytes, bytes.size, 0)
    }
}