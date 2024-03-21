package com.example.pointofsales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pointofsales.R
import com.example.pointofsales.data.SalesItem

class SalesAdapter(
    private val context: Context,
    private val onAddToCartClickListener: OnAddToCartClickListener
) : RecyclerView.Adapter<SalesAdapter.SalesItemViewHolder>() {

    private var items: List<SalesItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sales, parent, false)
        return SalesItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalesItemViewHolder, position: Int) {
        val currentItem = items[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(items: List<SalesItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    inner class SalesItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
        private val itemPriceTextView: TextView = itemView.findViewById(R.id.itemPriceTextView)
        private val itemQuantityTextView: TextView = itemView.findViewById(R.id.itemQuantityTextView)
        private val addToCartButton: ImageButton = itemView.findViewById(R.id.addToCartButton)

        fun bind(item: SalesItem) {
            itemNameTextView.text = item.itemName
            itemPriceTextView.text = "Price: ₱${item.itemPrice}"
            itemQuantityTextView.text = "Quantity: ${item.itemQuantity}"

            // Set onClickListener for addToCartButton
            addToCartButton.setOnClickListener {
                onAddToCartClickListener.onAddToCartClick(item)
            }
        }
    }

    interface OnAddToCartClickListener {
        fun onAddToCartClick(item: SalesItem)
    }
}
