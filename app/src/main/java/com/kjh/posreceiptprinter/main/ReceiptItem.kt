package com.kjh.posreceiptprinter.main

import com.kjh.posreceiptprinter.print.Justification
import com.kjh.posreceiptprinter.print.TableCell

class ReceiptItem(
    val product: String,
    var unitPrice: Int? = null,
    var quantity: Int? = null,
) {
    val price: Long?
        get() {
            val unitPrice = unitPrice ?: return null
            val amount = quantity ?: return null
            return unitPrice.toLong() * amount
        }

    fun setUnitPriceOrQuantity(value: Int) {
        if (unitPrice == null) {
            unitPrice = value
        } else if (quantity == null) {
            quantity = value
        }
    }

    fun toTableRow(): List<TableCell> {
        return listOf(
            TableCell(product, Justification.Left, 2),
            TableCell(unitPrice.format("0"), Justification.Right, 2),
            TableCell(quantity.format("0"), Justification.Right, 1),
            TableCell(price.format("0"), Justification.Right, 2),
        )
    }
}