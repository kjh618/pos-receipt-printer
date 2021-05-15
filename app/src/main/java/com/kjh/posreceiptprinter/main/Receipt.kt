package com.kjh.posreceiptprinter.main

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.kjh.posreceiptprinter.printing.PrintContent

class Receipt {
    val title: MutableLiveData<String> by lazy { MutableLiveData("영수증") }
    private val items: MutableList<ReceiptItem> = mutableListOf()
    private val totalPrice: MutableLiveData<Long> by lazy { MutableLiveData(0) }

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
        TODO()
    }
}