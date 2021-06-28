package com.kjh.posreceiptprinter.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kjh.posreceiptprinter.databinding.ReceiptItemBinding

class ReceiptItemsAdapter(
    private val receipt: Receipt,
    private val onSelectItem: (ReceiptItem?) -> Unit,
) : RecyclerView.Adapter<ReceiptItemsAdapter.ViewHolder>() {

    var selectedPosition: Int = RecyclerView.NO_POSITION
        private set(newSelectedPosition) {
            notifyItemChanged(field)
            field = newSelectedPosition
            notifyItemChanged(field)

            val item = if (field == RecyclerView.NO_POSITION) null else receipt.getItem(field)
            onSelectItem(item)
        }

    inner class ViewHolder(private val binding: ReceiptItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener { selectedPosition = layoutPosition }
        }

        fun bind(item: ReceiptItem, isSelected: Boolean) {
            binding.textViewProduct.text = item.product
            binding.textViewUnitPrice.text = item.unitPrice.format("")
            binding.textViewQuantity.text = item.quantity.format("")
            binding.textViewPrice.text = item.price.format("")

            binding.root.isActivated = isSelected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReceiptItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = receipt.getItem(position)
        holder.bind(item, selectedPosition == position)
    }

    override fun getItemCount(): Int {
        return receipt.itemCount
    }

    fun addItemWithProduct(product: String) {
        receipt.addItemWithProduct(product)
        notifyItemInserted(receipt.itemCount - 1)

        selectedPosition = receipt.itemCount - 1
    }

    fun setSelectedItemUnitPriceOrQuantity(value: Int) {
        if (selectedPosition == RecyclerView.NO_POSITION) {
            return
        }

        receipt.setItemUnitPriceOrQuantity(selectedPosition, value)
        notifyItemChanged(selectedPosition)

        selectedPosition = if (receipt.getItem(selectedPosition).price != null) {
            if (selectedPosition == receipt.itemCount - 1) { // last item completed
                RecyclerView.NO_POSITION
            } else { // middle item completed
                selectedPosition + 1
            }
        } else { // item not yet complete
            selectedPosition
        }
    }

    fun removeSelectedItem() {
        if (selectedPosition == RecyclerView.NO_POSITION) {
            return
        }

        receipt.removeItemAt(selectedPosition)
        notifyItemRemoved(selectedPosition)

        selectedPosition = when {
            receipt.itemCount == 0 -> // no items left
                RecyclerView.NO_POSITION
            selectedPosition == receipt.itemCount -> // last item removed
                selectedPosition - 1
            else -> // middle item removed
                selectedPosition
        }
    }

    fun clearItems() {
        receipt.clearItems()
        notifyDataSetChanged()
    }
}