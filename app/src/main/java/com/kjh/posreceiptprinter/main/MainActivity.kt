package com.kjh.posreceiptprinter.main

import android.content.Context
import android.content.Intent
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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kjh.posreceiptprinter.Printer
import com.kjh.posreceiptprinter.PrinterInfoActivity
import com.kjh.posreceiptprinter.R
import com.kjh.posreceiptprinter.databinding.ActivityMainBinding
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: MainViewModel
    private lateinit var receiptItemsAdapter: ReceiptItemsAdapter

    private fun initializePrinter() {
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

    private fun setupViews() {
        model.receipt.observeTotalPrice(this, {
            binding.textViewTotalPrice.text =
                getString(R.string.money_amount, NumberFormat.getInstance().format(it))
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

        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = ProductsAdapter(model.products)
        }

        model.currentNum.observe(this, { binding.textViewCurrentNum.text = it })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)
        model = ViewModelProvider(this).get(MainViewModel::class.java)

        Log.i(this::class.simpleName, "Locale: ${Locale.getDefault()}")

        if (!Printer.isInitialized) {
            initializePrinter()
        }

        setupViews()
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
        if (Printer.isInitialized) {
            // TODO: Print receipt
            Toast.makeText(applicationContext, "TODO", Toast.LENGTH_SHORT).show()

            receiptItemsAdapter.clearItems()
        } else {
            Toast.makeText(applicationContext, R.string.toast_no_printer, Toast.LENGTH_SHORT).show()
        }
    }
}