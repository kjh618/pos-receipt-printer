package com.kjh.posreceiptprinter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kjh.posreceiptprinter.databinding.ReceiptItemBinding

class ReceiptItemsAdapter : ListAdapter<ReceiptItem, ReceiptItemsAdapter.ViewHolder>(ReceiptItemDiffCallback) {
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    inner class ViewHolder(private val binding: ReceiptItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                notifyItemChanged(selectedPosition)
                selectedPosition = layoutPosition
                notifyItemChanged(selectedPosition)
            }
        }

        fun bind(receiptItem: ReceiptItem, isSelected: Boolean) {
            binding.textViewProduct.text = receiptItem.product
            binding.textViewUnitPrice.text = receiptItem.unitPrice.toString()
            binding.textViewAmount.text = receiptItem.amount.toString()
            binding.textViewTotalPrice.text = receiptItem.totalPrice.toString()

            binding.root.isActivated = isSelected
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReceiptItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val receiptItem = getItem(position)
        holder.bind(receiptItem, selectedPosition == position)
    }
}

object ReceiptItemDiffCallback : DiffUtil.ItemCallback<ReceiptItem>() {
    override fun areItemsTheSame(oldItem: ReceiptItem, newItem: ReceiptItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ReceiptItem, newItem: ReceiptItem): Boolean {
        return oldItem == newItem
    }
}