package com.kjh.posreceiptprinter.main

import android.content.res.Resources
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.kjh.posreceiptprinter.R
import com.kjh.posreceiptprinter.print.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class Receipt {
    private val items: MutableList<ReceiptItem> = mutableListOf()
    private val totalPrice: MutableLiveData<Long> by lazy { MutableLiveData(0) }

    val itemCount: Int
        get() = items.size

    fun observeTotalPrice(owner: LifecycleOwner, observer: Observer<Long>) {
        totalPrice.observe(owner, observer)
    }

    fun getItem(index: Int): ReceiptItem {
        return items[index]
    }

    fun addItemWithProduct(product: String) {
        val item = ReceiptItem(product)
        items.add(item)
    }

    fun setItemUnitPriceOrQuantity(index: Int, value: Int) {
        val item = items[index]
        item.setUnitPriceOrQuantity(value)
        totalPrice.value = totalPrice.value!! + (item.price ?: return)
    }

    fun removeItemAt(index: Int) {
        val item = items.removeAt(index)
        totalPrice.value = totalPrice.value!! - (item.price ?: return)
    }

    fun clearItems() {
        items.clear()
        totalPrice.value = 0
    }

    fun toPrintContent(res: Resources, title: String, footer: String): PrintContent {
        return PrintContent().apply {
            addCommand(PrinterCommand.Initialize)

            addCommand(PrinterCommand.SelectPrintModes(PrintModes(
                doubleHeight = true,
                doubleWidth = true,
            )))
            addCommand(PrinterCommand.SelectJustification(Justification.Center))
            addText(title + "\n\n")

            addCommand(PrinterCommand.SelectPrintModes(PrintModes()))
            addCommand(PrinterCommand.SelectJustification(Justification.Right))
            val dateTime =
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            addText(dateTime + "\n")

            addCommand(PrinterCommand.SelectJustification(Justification.Left))
            addLine('=')

            addTableRow(listOf(
                TableCell(res.getString(R.string.product_header), Justification.Center, 2),
                TableCell(res.getString(R.string.unit_price_header), Justification.Center, 2),
                TableCell(res.getString(R.string.quantity_header), Justification.Center, 1),
                TableCell(res.getString(R.string.price_header), Justification.Center, 2),
            ))

            addLine('-')

            items.forEach { addTableRow(it.toTableRow()) }

            addLine('-')

            addCommand(PrinterCommand.SelectPrintModes(PrintModes(doubleHeight = true)))
            addTableRow(listOf(
                TableCell(res.getString(R.string.total_price_header), Justification.Left, 2),
                TableCell(totalPrice.value!!.format("0"), Justification.Right, 5),
            ))

            addCommand(PrinterCommand.SelectPrintModes(PrintModes()))
            addLine('=')
            addText("\n")

            addText(footer + "\n")

            addCommand(PrinterCommand.PartialCut(100))
        }
    }
}

fun Int?.format(default: String): String {
    return if (this == null) default else NumberFormat.getInstance().format(this.toLong())
}

fun Long?.format(default: String): String {
    return if (this == null) default else NumberFormat.getInstance().format(this)
}