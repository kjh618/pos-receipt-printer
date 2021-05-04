package com.kjh.posreceiptprinter

import android.content.Context
import android.content.Intent
import android.hardware.usb.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.kjh.posreceiptprinter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var model: MainViewModel

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
            }
        }

        binding.recyclerViewProducts.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 5)
            adapter = ProductsAdapter(model.products, ::onButtonProductClick)
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

    private fun onButtonProductClick(product: String) {
        Toast.makeText(applicationContext, product, Toast.LENGTH_SHORT).show()
    }

    fun onButtonNumClick(view: View) {
        val button = view as Button
        model.currentNum.value = model.currentNum.value + button.text
    }

    fun onButtonDeleteClick(view: View) {
        model.currentNum.value = model.currentNum.value?.dropLast(1)
    }

    fun onButtonEnterClick(view: View) {
        Toast.makeText(applicationContext, model.currentNum.value, Toast.LENGTH_SHORT).show()
        model.currentNum.value = ""
    }

    fun print(view: View) {
        if (Printer.isInitialized) {
            Toast.makeText(applicationContext, "TODO", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(applicationContext, R.string.toast_no_printer, Toast.LENGTH_SHORT).show()
        }
    }
}