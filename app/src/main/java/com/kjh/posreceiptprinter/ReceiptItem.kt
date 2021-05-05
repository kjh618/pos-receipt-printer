package com.kjh.posreceiptprinter

data class ReceiptItem(
    val id: Int,
    val product: String,
    val unitPrice: Int? = null,
    val amount: Int? = null,
) {
    val totalPrice: Int?
        get() = if (unitPrice != null && amount != null) unitPrice * amount else null
}