package com.kjh.posreceiptprinter

import android.content.Context
import android.content.Intent
import android.hardware.usb.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var printer: Printer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbarMain))

        val manager = getSystemService(Context.USB_SERVICE) as UsbManager
        val device = intent.getParcelableExtra<UsbDevice>(UsbManager.EXTRA_DEVICE)
        if (device == null) {
            val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.dialog_no_usb_title)
                .setMessage(R.string.dialog_no_usb_message)
                .setPositiveButton(R.string.dialog_ok) { _, _ -> finishAndRemoveTask() }
//                .setOnDismissListener { finishAndRemoveTask() }
                .create()
            dialog.show()
            return
        }
        printer = Printer(manager, device)

        val products = Array(12) { it.toString() }
        val recyclerViewProducts = findViewById<RecyclerView>(R.id.recyclerViewProducts)
        recyclerViewProducts.layoutManager = GridLayoutManager(this, 5)
        recyclerViewProducts.adapter = RecyclerViewProductsAdapter(products, ::onProductButtonClick)
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

    private fun onProductButtonClick(view: View) {
        val textView = view as TextView
        Toast.makeText(applicationContext, textView.text, Toast.LENGTH_SHORT).show()
    }
}