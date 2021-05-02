package com.kjh.posreceiptprinter

import android.hardware.usb.*

object Printer {
    private lateinit var usbDevice: UsbDevice
    private lateinit var usbInterface: UsbInterface
    private lateinit var usbEndpoint: UsbEndpoint
    private lateinit var usbConnection: UsbDeviceConnection
    val isInitialized: Boolean
        get() = Printer::usbDevice.isInitialized
            && Printer::usbInterface.isInitialized
            && Printer::usbEndpoint.isInitialized
            && Printer::usbConnection.isInitialized

    fun init(manager: UsbManager, device: UsbDevice) {
        usbDevice = device
        // TODO: Search for valid interface/endpoint instead of using index 0
        usbInterface = usbDevice.getInterface(0)
        usbEndpoint = usbInterface.getEndpoint(0)
        usbConnection = manager.openDevice(usbDevice)
        usbConnection.claimInterface(usbInterface, true)
    }

    override fun toString(): String {
        return if (isInitialized) {
            "Printer(usbDevice=$usbDevice, ...)"
        } else {
            "printer not initialized"
        }
    }

    fun print(bytes: ByteArray) {
        usbConnection.bulkTransfer(usbEndpoint, bytes, bytes.size, 0)
    }
}