package com.kjh.posreceiptprinter

data class ReceiptItem(
    val id: Long,
    val product: String,
    var unitPrice: Int? = null,
    var amount: Int? = null,
) {
    val totalPrice: Int?
        get() {
            val unitPrice = unitPrice ?: return null
            val amount = amount ?: return null
            return unitPrice * amount
        }
    val isComplete: Boolean
        get() = unitPrice != null && amount != null

    fun setUnitPriceOrAmount(value: Int) {
        if (unitPrice == null) {
            unitPrice = value
        } else if (amount == null) {
            amount = value
        }
    }
}