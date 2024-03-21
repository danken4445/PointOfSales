package com.example.pointofsales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pointofsales.R
import com.example.pointofsales.data.CartItem

class CartAdapter : RecyclerView.Adapter<CartAdapter.CartItemViewHolder>() {

    private var items: MutableList<CartItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        val currentItem = items[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(cartItems: List<CartItem>) {
        items.clear()
        items.addAll(cartItems)
        notifyDataSetChanged()
    }

    inner class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameCart)
        private val itemPriceTextView: TextView = itemView.findViewById(R.id.itemPriceCart)
        private val itemQuantityTextView: TextView = itemView.findViewById(R.id.itemQuantityTextViewCart)

        fun bind(item: CartItem) {
            itemNameTextView.text = item.itemName
            itemPriceTextView.text = "₱${item.itemPrice}"
            itemQuantityTextView.text = "Quantity: ${item.quantity}"
        }
    }
}
