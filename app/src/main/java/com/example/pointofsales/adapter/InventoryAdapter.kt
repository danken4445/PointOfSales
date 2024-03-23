package com.example.pointofsales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pointofsales.R
import com.example.pointofsales.data.InventoryItem
import com.example.pointofsales.fragments.AddItemFormFragment

class InventoryAdapter(private val context: Context) : RecyclerView.Adapter<InventoryAdapter.InventoryItemViewHolder>() {

    private var items: List<InventoryItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_inventory, parent, false)
        return InventoryItemViewHolder(view)

    }

    override fun onBindViewHolder(holder: InventoryItemViewHolder, position: Int) {
        val currentItem = items[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(items: List<InventoryItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class InventoryItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
        private val itemPriceTextView: TextView = itemView.findViewById(R.id.itemPriceTextView)
        private val itemQuantityTextView: TextView = itemView.findViewById(R.id.itemQuantityTextView)

        fun bind(item: InventoryItem) {
            itemNameTextView.text = "${item.itemName}"
            itemPriceTextView.text = "Price: ₱${item.itemPrice}"
            itemQuantityTextView.text = "Quantity: ${item.itemQuantity}"
            // Load item image using Picasso or any other image loading library
        }
    }

}
