package com.kjh.posreceiptprinter.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val receipt: Receipt = Receipt()

    val products: Array<String> = Array(11) { "상품 ${it + 1}" }

    val currentNum: MutableLiveData<String> by lazy { MutableLiveData("") }
}