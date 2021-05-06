package com.kjh.posreceiptprinter

import android.content.Context
import android.content.Intent
import android.hardware.usb.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
                Log.w("MainActivity", "No USB device detected")
                Toast.makeText(applicationContext, R.string.toast_no_printer, Toast.LENGTH_SHORT)
                    .show()
            }
        }

        receiptItemsAdapter = ReceiptItemsAdapter()
        model.receipt.observe(this, {
            receiptItemsAdapter.submitList(it)
            binding.recyclerViewReceipt.smoothScrollToPosition(it.size)
        })
        binding.recyclerViewReceipt.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = receiptItemsAdapter
        }

        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 5)
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
            R.id.menuItemDebug -> {
                val intent = Intent(this, DebugActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.menuItemSettings -> {
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    fun onClickButtonProduct(view: View) {
        model.receiptItemId++
        val product = (view as Button).text.toString()
        val newReceiptItem = ReceiptItem(model.receiptItemId, product)
        model.receipt.value = model.receipt.value!! + newReceiptItem

        receiptItemsAdapter.selectedPosition = model.receipt.value!!.size - 1

        Log.d("MainActivity", "Added $newReceiptItem to receipt")
    }

    fun onClickButtonDigits(view: View) {
        val newDigits = (view as Button).text
        model.currentNum.value = model.currentNum.value!! + newDigits
    }

    fun onClickButtonDelete(view: View) {
        model.currentNum.value = model.currentNum.value!!.dropLast(1)
    }

    fun onClickButtonEnter(view: View) {
        val value = model.currentNum.value!!.toInt()

        with(receiptItemsAdapter) {
            selectedItem.setUnitPriceOrAmount(value)
            notifyItemChanged(selectedPosition)

            if (selectedItem.isComplete) {
                selectedPosition = RecyclerView.NO_POSITION
                binding.textViewTotalPrice.text = model.receipt.value!!
                    .sumOf { it.price ?: 0 }
                    .toString()
            }
        }

        model.currentNum.value = ""
    }

    fun onClickButtonPrint(view: View) {
        if (Printer.isInitialized) {
            // TODO
            Toast.makeText(applicationContext, "TODO", Toast.LENGTH_SHORT).show()
            model.receipt.value = emptyList()
            model.receiptItemId = 0
        } else {
            Toast.makeText(applicationContext, R.string.toast_no_printer, Toast.LENGTH_SHORT).show()
        }
    }
}