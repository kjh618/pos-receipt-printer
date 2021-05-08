package com.kjh.posreceiptprinter.main

import android.content.Context
import android.content.Intent
import android.hardware.usb.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kjh.posreceiptprinter.Printer
import com.kjh.posreceiptprinter.PrinterInfoActivity
import com.kjh.posreceiptprinter.R
import com.kjh.posreceiptprinter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: MainViewModel
    private lateinit var receiptItemsAdapter: ReceiptItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)
        model = ViewModelProvider(this).get(MainViewModel::class.java)

        if (!Printer.isInitialized) {
            val manager = getSystemService(Context.USB_SERVICE) as UsbManager
            val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
            if (device != null) {
                Printer.initialize(manager, device)
            } else {
                Log.w(this::class.simpleName, "No USB device detected")
                Toast.makeText(applicationContext, R.string.toast_no_printer, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        model.receipt.observeTotalPrice(this, { binding.textViewTotalPrice.text = it.toString() })
        receiptItemsAdapter = ReceiptItemsAdapter(model.receipt)
        binding.recyclerViewReceiptItems.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = receiptItemsAdapter
        }

        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            adapter = ProductsAdapter(model.products)
        }

        model.currentNum.observe(this, { binding.textViewCurrentNum.text = it })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuItemPrinterInfo -> {
                val intent = Intent(this, PrinterInfoActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuItemSettings -> {
                // TODO: Settings
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    fun onClickButtonRemoveReceiptItem(@Suppress("UNUSED_PARAMETER") view: View) {
        receiptItemsAdapter.removeSelectedItem()
        if (receiptItemsAdapter.selectedPosition != RecyclerView.NO_POSITION) {
            binding.recyclerViewReceiptItems.smoothScrollToPosition(receiptItemsAdapter.selectedPosition)
        }
    }

    fun onClickButtonProduct(view: View) {
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
        if (Printer.isInitialized) {
            // TODO: Print receipt
            Toast.makeText(applicationContext, "TODO", Toast.LENGTH_SHORT).show()

            receiptItemsAdapter.clearItems()
        } else {
            Toast.makeText(applicationContext, R.string.toast_no_printer, Toast.LENGTH_SHORT).show()
        }
    }
}