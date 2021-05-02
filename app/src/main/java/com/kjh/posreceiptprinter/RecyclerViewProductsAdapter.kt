package com.kjh.posreceiptprinter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kjh.posreceiptprinter.databinding.ButtonProductBinding

class RecyclerViewProductsAdapter(
    private val products: Array<String>,
    private val onClick: (String) -> Unit,
) : RecyclerView.Adapter<RecyclerViewProductsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ButtonProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: String, onClick: (String) -> Unit) {
            binding.buttonProduct.text = product
            binding.buttonProduct.setOnClickListener { onClick(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ButtonProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position], onClick)
    }

    override fun getItemCount(): Int {
        return products.size
    }
}