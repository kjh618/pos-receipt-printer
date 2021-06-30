package com.kjh.posreceiptprinter.main

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kjh.posreceiptprinter.printerinfo.PrinterInfoActivity
import com.kjh.posreceiptprinter.R
import com.kjh.posreceiptprinter.databinding.ActivityMainBinding
import com.kjh.posreceiptprinter.print.*
import com.kjh.posreceiptprinter.settings.SettingsActivity
import com.kjh.posreceiptprinter.settings.parseProductsPreference
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: MainViewModel
    private lateinit var receiptItemsAdapter: ReceiptItemsAdapter

    private val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                "title" -> supportActionBar!!.title = sharedPreferences.getString(key, null)!!
                "products" -> model.products.value =
                    parseProductsPreference(sharedPreferences.getString(key, null)!!)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)
        model = ViewModelProvider(this).get(MainViewModel::class.java)

        Log.i(this::class.simpleName, "Locale: ${Locale.getDefault()}")

        if (savedInstanceState == null) {
            if (!PrinterManager.isInitialized) {
                PrinterManager.initialize(applicationContext)
            }
            if (PrinterManager.printer == null) {
                connectUsbPrinter()
            }
        }

        setupProducts()
        setupReceipt()
        model.currentNum.observe(this, { binding.textViewCurrentNum.text = it })

        PreferenceManager.setDefaultValues(this, R.xml.settings, true)
        PreferenceManager.getDefaultSharedPreferences(this).apply {
            registerOnSharedPreferenceChangeListener(listener)
            listener.onSharedPreferenceChanged(this, "title")
            listener.onSharedPreferenceChanged(this, "products")
        }
    }

    override fun onNewIntent(newIntent: Intent) {
        super.onNewIntent(newIntent)
        intent = newIntent
        connectUsbPrinter()
    }

    private fun connectUsbPrinter() {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_USB_HOST)) {
            val manager = getSystemService(Context.USB_SERVICE) as UsbManager
            val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
            if (device != null) {
                PrinterManager.printer = UsbPrinter(manager, device)
                return
            }
        }

        Toast.makeText(applicationContext, R.string.toast_usb_printer_connection_failed, Toast.LENGTH_SHORT)
            .show()
        Log.w(this::class.simpleName, "Failed to connect to USB printer")
    }

    private fun setupReceipt() {
        model.receipt.observeTotalPrice(this, {
            binding.textViewTotalPrice.text = getString(R.string.money_amount, it.format("0"))
        })

        receiptItemsAdapter = ReceiptItemsAdapter(model.receipt) {
            binding.textViewCurrentNumHeader.text = when {
                it == null -> ""
                it.unitPrice == null -> getString(
                    R.string.text_view_current_num_header,
                    getString(R.string.unit_price_header),
                )
                it.quantity == null -> getString(
                    R.string.text_view_current_num_header,
                    getString(R.string.quantity_header),
                )
                else -> ""
            }
        }

        binding.recyclerViewReceiptItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = receiptItemsAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            itemAnimator?.changeDuration = 0
        }
    }

    private fun setupProducts() {
        val productsAdapter = ProductsAdapter(model.products)

        model.products.observe(this, { productsAdapter.notifyDataSetChanged() })

        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = productsAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuItemConnectBluetooth -> {
                connectBluetoothPrinter()
                true
            }
            R.id.menuItemPrinterInfo -> {
                startActivity(Intent(this, PrinterInfoActivity::class.java))
                true
            }
            R.id.menuItemPrinterTest -> {
                PrinterManager.printAndDoIfSuccessful({ TEST_CONTENT.toByteArray() })
                true
            }
            R.id.menuItemSettings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun connectBluetoothPrinter() {
        val toastConnectionFailed =
            Toast.makeText(applicationContext, R.string.toast_bluetooth_printer_connection_failed, Toast.LENGTH_SHORT)

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            val adapter = BluetoothAdapter.getDefaultAdapter()!!
            val device = adapter.bondedDevices.elementAt(0) // TODO: Search name?
            try {
                PrinterManager.printer = BluetoothPrinter(device)
            } catch (e: IOException) {
                toastConnectionFailed.show()
                Log.w(this::class.simpleName, "Failed to connect to Bluetooth printer: $e")
            }
            return
        }

        toastConnectionFailed.show()
        Log.w(this::class.simpleName, "Failed to connect to Bluetooth printer")
    }

    fun onClickButtonRemoveReceiptItem(@Suppress("UNUSED_PARAMETER") view: View) {
        if (receiptItemsAdapter.selectedPosition == RecyclerView.NO_POSITION) {
            return
        }

        binding.recyclerViewReceiptItems.itemAnimator?.endAnimations()
        receiptItemsAdapter.removeSelectedItem()
        if (receiptItemsAdapter.selectedPosition != RecyclerView.NO_POSITION) {
            binding.recyclerViewReceiptItems.smoothScrollToPosition(receiptItemsAdapter.selectedPosition)
        }
    }

    fun onClickButtonProduct(view: View) {
        binding.recyclerViewReceiptItems.itemAnimator?.endAnimations()
        val product = (view as Button).text.toString()
        receiptItemsAdapter.addItemWithProduct(product)
        binding.recyclerViewReceiptItems.smoothScrollToPosition(receiptItemsAdapter.selectedPosition)
    }

    fun onClickButtonDigits(view: View) {
        val newDigits = (view as Button).text
        model.currentNum.value = model.currentNum.value!! + newDigits
    }

    fun onClickButtonDeleteDigit(@Suppress("UNUSED_PARAMETER") view: View) {
        model.currentNum.value = model.currentNum.value!!.dropLast(1)
    }

    fun onClickButtonEnter(@Suppress("UNUSED_PARAMETER") view: View) {
        if (receiptItemsAdapter.selectedPosition == RecyclerView.NO_POSITION) {
            return
        }

        val value = model.currentNum.value!!.toIntOrNull() ?: return
        binding.recyclerViewReceiptItems.smoothScrollToPosition(receiptItemsAdapter.selectedPosition)
        receiptItemsAdapter.setSelectedItemUnitPriceOrQuantity(value)

        model.currentNum.value = ""
    }

    fun onClickButtonPrint(@Suppress("UNUSED_PARAMETER") view: View) {
        PrinterManager.printAndDoIfSuccessful(
            {
                val prefs = PreferenceManager.getDefaultSharedPreferences(this)
                val title = prefs.getString("title", null)!!
                val footer = prefs.getString("footer", null)!!
                model.receipt.toPrintContent(resources, title, footer).toByteArray()
            },
            { receiptItemsAdapter.clearItems() },
        )
    }
}