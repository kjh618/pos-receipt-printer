package com.kjh.posreceiptprinter.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val receipt: Receipt = Receipt()

    val products: MutableLiveData<List<String>> by lazy { MutableLiveData(listOf("상품 1")) }

    val currentNum: MutableLiveData<String> by lazy { MutableLiveData("") }
}