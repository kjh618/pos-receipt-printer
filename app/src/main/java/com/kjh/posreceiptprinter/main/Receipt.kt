package com.kjh.posreceiptprinter.main

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.kjh.posreceiptprinter.printing.Justification
import com.kjh.posreceiptprinter.printing.PrintContent
import com.kjh.posreceiptprinter.printing.PrintModes
import com.kjh.posreceiptprinter.printing.PrinterCommand

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

    fun toPrintContent(): PrintContent {
        return PrintContent().apply {
            addCommand(PrinterCommand.Initialize)

            addText("Test 테스트\n")

            addCommand(PrinterCommand.SelectPrintModes(PrintModes(
                false, true, true, true, true
            )))
            addText("Test 테스트\n")

            addCommand(PrinterCommand.SelectPrintModes(PrintModes()))
            addCommand(PrinterCommand.SelectJustification(Justification.Left))
            addText("Test 테스트\n")
            addCommand(PrinterCommand.SelectJustification(Justification.Center))
            addText("Test 테스트\n")
            addCommand(PrinterCommand.SelectJustification(Justification.Right))
            addText("Test 테스트\n")

            addCommand(PrinterCommand.PartialCut(100))
        }
    }
}