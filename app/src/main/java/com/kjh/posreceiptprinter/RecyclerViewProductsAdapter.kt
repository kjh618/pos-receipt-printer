package com.kjh.posreceiptprinter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewProductsAdapter(
    private val products: Array<Product>,
    private val onClick: (Product) -> Unit,
) : RecyclerView.Adapter<RecyclerViewProductsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val buttonProduct: Button = view.findViewById(R.id.buttonProduct)

        fun bind(product: Product, onClick: (Product) -> Unit) {
            buttonProduct.text = "${product.name}\n${product.price}"
            buttonProduct.setOnClickListener { onClick(product) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_button, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position], onClick)
    }

    override fun getItemCount(): Int {
        return products.size
    }
}