package com.kjh.posreceiptprinter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val products: Array<String> = Array(12) { "상품 ${it + 1}" }
    val currentNum: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }
}