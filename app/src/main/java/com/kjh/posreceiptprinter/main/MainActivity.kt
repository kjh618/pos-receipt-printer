package com.kjh.posreceiptprinter.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import com.kjh.posreceiptprinter.PrinterInfoActivity
import com.kjh.posreceiptprinter.R
import com.kjh.posreceiptprinter.databinding.ActivityMainBinding
import com.kjh.posreceiptprinter.printing.PrintManager
import com.kjh.posreceiptprinter.printing.TEST_CONTENT
import com.kjh.posreceiptprinter.settings.SettingsActivity
import com.kjh.posreceiptprinter.settings.parseProductsPreference
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: MainViewModel
    private lateinit var receiptItemsAdapter: ReceiptItemsAdapter

    private val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                "title" -> model.receipt.title.value = sharedPreferences.getString(key, null)!!
                "products" -> model.products.value =
                    parseProductsPreference(sharedPreferences.getString(key, null)!!)
                "footer" -> model.receipt.footer = sharedPreferences.getString(key, null)!!
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)
        model = ViewModelProvider(this).get(MainViewModel::class.java)

        Log.i(this::class.simpleName, "Locale: ${Locale.getDefault()}")

        setupPrinter()

        setupProducts()
        setupReceipt()
        model.currentNum.observe(this, { binding.textViewCurrentNum.text = it })

        PreferenceManager.getDefaultSharedPreferences(this).apply {
            registerOnSharedPreferenceChangeListener(listener)

            model.receipt.title.value = getString("title", "영수증")!!
            model.products.value = parseProductsPreference(getString("products", "상품 1")!!)
            model.receipt.footer = getString("footer", "영수증")!!
        }
    }

    private fun setupPrinter() {
        if (!PrintManager.isPrinterInitialized) {
            val manager = getSystemService(Context.USB_SERVICE) as UsbManager
            val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
            if (device != null) {
                PrintManager.initializeUsbPrinter(manager, device)
            } else {
                Log.w(this::class.simpleName, "No USB device detected")
                Toast.makeText(applicationContext, R.string.toast_no_printer, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setupReceipt() {
        model.receipt.res = resources
        model.receipt.observe(
            this,
            { supportActionBar!!.title = "${getString(R.string.app_name)} - $it" },
            {
                binding.textViewTotalPrice.text =
                    getString(R.string.money_amount, it.format("0"))
            },
        )

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
            R.id.menuItemPrinterInfo -> {
                startActivity(Intent(this, PrinterInfoActivity::class.java))
                true
            }
            R.id.menuItemPrinterTest -> {
                printTestContent()
                true
            }
            R.id.menuItemSettings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun printTestContent() {
        if (PrintManager.isPrinterInitialized) {
            Toast.makeText(applicationContext, R.string.toast_printing, Toast.LENGTH_SHORT).show()
            PrintManager.printer.print(TEST_CONTENT.toByteArray())
        } else {
            Toast.makeText(applicationContext, R.string.toast_no_printer, Toast.LENGTH_SHORT).show()
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
        if (PrintManager.isPrinterInitialized) {
            Toast.makeText(applicationContext, R.string.toast_printing, Toast.LENGTH_SHORT).show()
            PrintManager.printer.print(model.receipt.toPrintContent().toByteArray())
            receiptItemsAdapter.clearItems()
        } else {
            Toast.makeText(applicationContext, R.string.toast_no_printer, Toast.LENGTH_SHORT).show()
        }
    }
}