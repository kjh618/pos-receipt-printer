package com.kjh.posreceiptprinter

data class ReceiptItem(
    val id: Long,
    val product: String,
    var unitPrice: Int? = null,
    var quantity: Int? = null,
) {
    val price: Int?
        get() {
            val unitPrice = unitPrice ?: return null
            val amount = quantity ?: return null
            return unitPrice * amount
        }
    val isComplete: Boolean
        get() = unitPrice != null && quantity != null

    fun setUnitPriceOrAmount(value: Int) {
        if (unitPrice == null) {
            unitPrice = value
        } else if (quantity == null) {
            quantity = value
        }
    }
}