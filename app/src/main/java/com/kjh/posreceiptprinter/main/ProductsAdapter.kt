package com.kjh.posreceiptprinter.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.kjh.posreceiptprinter.databinding.ButtonProductBinding

class ProductsAdapter(private val products: LiveData<List<String>>) :
    RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ButtonProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: String) {
            binding.buttonProduct.text = product
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ButtonProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products.value!![position])
    }

    override fun getItemCount(): Int {
        return products.value!!.size
    }
}