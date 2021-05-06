package com.kjh.posreceiptprinter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kjh.posreceiptprinter.databinding.ReceiptItemBinding

class ReceiptItemsAdapter(private val receipt: MutableList<ReceiptItem>) :
    RecyclerView.Adapter<ReceiptItemsAdapter.ViewHolder>() {
    var selectedPosition: Int = RecyclerView.NO_POSITION
        set(newSelectedPosition) {
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
        val item = receipt[position]
        holder.bind(item, selectedPosition == position)
    }

    override fun getItemCount(): Int {
        return receipt.size
    }

    fun addItem(item: ReceiptItem) {
        receipt.add(item)
        notifyItemInserted(receipt.size - 1)
        selectedPosition = receipt.size - 1
    }

    fun setSelectedItemUnitPriceOrAmount(value: Int): Boolean {
        if (selectedPosition == RecyclerView.NO_POSITION) {
            return false
        }

        val selectedItem = receipt[selectedPosition]
        selectedItem.setUnitPriceOrAmount(value)
        notifyItemChanged(selectedPosition)
        if (selectedItem.isComplete) {
            selectedPosition = RecyclerView.NO_POSITION
        }

        return selectedItem.isComplete
    }

    fun removeSelectedItem() {
        if (selectedPosition == RecyclerView.NO_POSITION) {
            return
        }

        receipt.removeAt(selectedPosition)
        notifyItemRemoved(selectedPosition)
        selectedPosition = when {
            receipt.isEmpty() -> RecyclerView.NO_POSITION
            selectedPosition == receipt.size -> selectedPosition - 1
            else -> selectedPosition
        }
    }

    fun clearItems() {
        receipt.clear()
        notifyDataSetChanged()
    }
}