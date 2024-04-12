package com.example.pointofsales.adapter.history

import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pointofsales.R
import com.example.pointofsales.data.InventoryItem
import java.text.NumberFormat
import java.util.Locale

class ProductsAdapter : ListAdapter<InventoryItem, ProductsAdapter.ProductsViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductsViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    inner class ProductsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemNameTextView: TextView = itemView.findViewById(R.id.textViewItemName)
        private val itemPriceTextView: TextView = itemView.findViewById(R.id.textViewItemPrice)
        private val itemQuantityTextView: TextView = itemView.findViewById(R.id.textViewItemQuantity)

        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH"))

        fun bind(product: InventoryItem) {
            itemNameTextView.text = Html.fromHtml("Item Name: <b>${product.itemName}</b>")
            val price = product.itemPrice.toDoubleOrNull() ?: 0.0


            // Format item price as currency
            val formattedPrice = currencyFormat.format(price)
            itemPriceTextView.text = Html.fromHtml("Price: <b>$formattedPrice</b>")

            itemQuantityTextView.text = Html.fromHtml("Quantity: <b>${product.itemQuantity}</b>")
        }
    }


    object DiffCallback : DiffUtil.ItemCallback<InventoryItem>() {
        override fun areItemsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
            return oldItem.itemName == newItem.itemName
        }

        override fun areContentsTheSame(oldItem: InventoryItem, newItem: InventoryItem): Boolean {
            return oldItem == newItem
        }
    }
}
