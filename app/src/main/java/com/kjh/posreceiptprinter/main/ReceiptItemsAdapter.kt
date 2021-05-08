package com.kjh.posreceiptprinter.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kjh.posreceiptprinter.databinding.ReceiptItemBinding

class ReceiptItemsAdapter(private val receipt: Receipt) :
    RecyclerView.Adapter<ReceiptItemsAdapter.ViewHolder>() {
    var selectedPosition: Int = RecyclerView.NO_POSITION
        private set(newSelectedPosition) {
            notifyItemChanged(field)
            field = newSelectedPosition
            notifyItemChanged(field)
        }

    inner class ViewHolder(private val binding: ReceiptItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener { selectedPosition = layoutPosition }
        }

        fun bind(item: ReceiptItem, isSelected: Boolean) {
            binding.textViewProduct.text = item.product
            binding.textViewUnitPrice.text = item.unitPrice?.toString()
            binding.textViewQuantity.text = item.quantity?.toString()
            binding.textViewPrice.text = item.price?.toString()

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
        if (receipt.getItem(selectedPosition).price != null) { // item completed
            selectedPosition = RecyclerView.NO_POSITION
        }
    }

    fun removeSelectedItem() {
        if (selectedPosition == RecyclerView.NO_POSITION) {
            return
        }

        receipt.removeItemAt(selectedPosition)
        notifyItemRemoved(selectedPosition)
        selectedPosition = when {
            receipt.itemCount == 0 -> RecyclerView.NO_POSITION
            selectedPosition == receipt.itemCount -> selectedPosition - 1
            else -> selectedPosition
        }
    }

    fun clearItems() {
        receipt.clearItems()
        notifyDataSetChanged()
    }
}