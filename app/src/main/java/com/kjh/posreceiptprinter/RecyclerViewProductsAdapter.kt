package com.kjh.posreceiptprinter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewProductsAdapter(
    private val products: Array<String>,
    private val onClick: (View) -> Unit,
) : RecyclerView.Adapter<RecyclerViewProductsAdapter.ViewHolder>() {
    class ViewHolder(view: View, onClick: (View) -> Unit) : RecyclerView.ViewHolder(view) {
        val buttonProduct: Button = view.findViewById(R.id.buttonProduct)
        init {
            buttonProduct.setOnClickListener(onClick)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_button, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.buttonProduct.text = products[position]
    }

    override fun getItemCount(): Int {
        return products.size
    }
}