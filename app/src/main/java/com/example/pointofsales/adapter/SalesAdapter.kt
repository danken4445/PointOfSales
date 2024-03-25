package com.example.pointofsales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
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
        private val itemQuantityTextView: TextView =
            itemView.findViewById(R.id.itemQuantityTextView)
        private val addToCartButton: ImageButton = itemView.findViewById(R.id.addToCartButton)

        fun bind(item: SalesItem) {
            itemNameTextView.text = item.itemName
            itemPriceTextView.text = "Price: ₱${item.itemPrice}"
            itemQuantityTextView.text = "Quantity: ${item.itemQuantity}"

            // Set onClickListener for addToCartButton
            addToCartButton.setOnClickListener {
                // Check if the item quantity is greater than 0 before decreasing it
                if (item.itemQuantity > 0) {
                    // Decrease the item quantity by 1
                    item.itemQuantity--
                    // Update the quantity TextView
                    itemQuantityTextView.text = "Quantity: ${item.itemQuantity}"
                    // Trigger the add to cart click event
                    onAddToCartClickListener.onAddToCartClick(item)
                } else {
                    // Notify the user that the item is out of stock
                    Toast.makeText(context, "Item is out of stock", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    interface OnAddToCartClickListener {
        fun onAddToCartClick(item: SalesItem)
    }
}
