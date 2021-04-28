package com.kjh.posreceiptprinter

import android.content.Context
import android.content.Intent
import android.hardware.usb.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog

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
}