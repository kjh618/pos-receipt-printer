package com.kjh.posreceiptprinter.main

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer

class Receipt {
    private val items: MutableList<ReceiptItem> = mutableListOf()
    private var newItemId: Long = 1
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
        val item = ReceiptItem(newItemId, product)
        items.add(item)
        newItemId++
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
        newItemId = 1
        totalPrice.value = 0
    }
}