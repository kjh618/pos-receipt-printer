package com.kjh.posreceiptprinter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kjh.posreceiptprinter.databinding.ReceiptItemBinding

class ReceiptItemsAdapter : ListAdapter<ReceiptItem, ReceiptItemsAdapter.ViewHolder>(ReceiptItemDiffCallback) {

    class ViewHolder(private val binding: ReceiptItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(receiptItem: ReceiptItem) {
            binding.textViewProduct.text = receiptItem.product
            binding.textViewUnitPrice.text = receiptItem.unitPrice.toString()
            binding.textViewAmount.text = receiptItem.amount.toString()
            binding.textViewTotalPrice.text = receiptItem.totalPrice.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ReceiptItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
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