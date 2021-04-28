package com.kjh.posreceiptprinter

import android.hardware.usb.*

class Printer(usbManager: UsbManager, private val usbDevice: UsbDevice) {
    // TODO: Search for valid interface/endpoint instead of using index 0
    private val usbInterface = usbDevice.getInterface(0)
    private val usbEndpoint = usbInterface.getEndpoint(0)
    private val usbConnection = usbManager.openDevice(usbDevice)

    init {
        usbConnection.claimInterface(usbInterface, true)
    }

    fun usbDeviceToString(): String {
        return usbDevice.toString()
    }

    fun print(bytes: ByteArray) {
        usbConnection.bulkTransfer(usbEndpoint, bytes, bytes.size, 0)
    }
}