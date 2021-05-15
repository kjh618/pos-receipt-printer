package com.kjh.posreceiptprinter.main

import android.content.res.Resources
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.kjh.posreceiptprinter.R
import com.kjh.posreceiptprinter.printing.*
import java.text.NumberFormat

class Receipt {
    val title: MutableLiveData<String> by lazy { MutableLiveData("영수증") }
    private val items: MutableList<ReceiptItem> = mutableListOf()
    private val totalPrice: MutableLiveData<Long> by lazy { MutableLiveData(0) }

    lateinit var res: Resources

    val itemCount: Int
        get() = items.size

    fun observe(
        owner: LifecycleOwner,
        titleObserver: Observer<String>,
        totalPriceObserver: Observer<Long>,
    ) {
        title.observe(owner, titleObserver)
        totalPrice.observe(owner, totalPriceObserver)
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

    fun toPrintContent(): PrintContent {
        return PrintContent().apply {
            addCommand(PrinterCommand.Initialize)

            addCommand(PrinterCommand.SelectPrintModes(PrintModes(doubleHeight = true, doubleWidth = true)))
            addCommand(PrinterCommand.SelectJustification(Justification.Center))
            addText(title.value!! + "\n\n")

            addCommand(PrinterCommand.SelectPrintModes(PrintModes()))
            addText("=".repeat(CPL_FONT_A) + "\n")

            addTableRow(listOf(
                TableCell(res.getString(R.string.product_header), Justification.Center, 2),
                TableCell(res.getString(R.string.unit_price_header), Justification.Center, 2),
                TableCell(res.getString(R.string.quantity_header), Justification.Center, 1),
                TableCell(res.getString(R.string.price_header), Justification.Center, 2),
            ))

            addText("-".repeat(CPL_FONT_A) + "\n")

            items.forEach { addTableRow(it.toTableRow()) }

            addText("-".repeat(CPL_FONT_A) + "\n")

            addCommand(PrinterCommand.SelectPrintModes(PrintModes(doubleHeight = true)))
            addTableRow(listOf(
                TableCell(res.getString(R.string.total_price_header), Justification.Left, 1),
                TableCell(totalPrice.value!!.format("0"), Justification.Right, 2),
            ))

            addCommand(PrinterCommand.SelectPrintModes(PrintModes()))
            addText("=".repeat(CPL_FONT_A) + "\n")

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