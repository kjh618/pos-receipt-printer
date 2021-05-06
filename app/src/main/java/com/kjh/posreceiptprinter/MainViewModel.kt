package com.kjh.posreceiptprinter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val receipt: MutableLiveData<List<ReceiptItem>> by lazy {
        MutableLiveData(emptyList())
    }
    var receiptItemId: Long = 0

    val products: Array<String> = Array(11) { "상품 ${it + 1}" }

    val currentNum: MutableLiveData<String> by lazy {
        MutableLiveData("")
    }
}