package com.kjh.posreceiptprinter.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kjh.posreceiptprinter.print.Printer

class MainViewModel : ViewModel() {
    val printer: MutableLiveData<Printer?> by lazy { MutableLiveData(null) }

    val receipt: Receipt = Receipt()
    val products: MutableLiveData<List<String>> by lazy { MutableLiveData(listOf()) }
    val currentNum: MutableLiveData<String> by lazy { MutableLiveData("") }
}