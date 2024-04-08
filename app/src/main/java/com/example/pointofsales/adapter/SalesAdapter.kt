package com.example.pointofsales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.pointofsales.R
import com.example.pointofsales.data.SalesItem
import com.squareup.picasso.Picasso
import java.util.Locale

class SalesAdapter(
    private val context: Context,
    private val onAddToCartClickListener: OnAddToCartClickListener
) : RecyclerView.Adapter<SalesAdapter.SalesItemViewHolder>(), Filterable {

    private var items: List<SalesItem> = listOf()
    private var filteredItems: List<SalesItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sales, parent, false)
        return SalesItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: SalesItemViewHolder, position: Int) {
        val currentItem = filteredItems[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return filteredItems.size
    }

    fun setItems(items: List<SalesItem>) {
        this.items = items
        this.filteredItems = items
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<SalesItem>()
                val query = constraint.toString().toLowerCase(Locale.ROOT).trim()
                if (query.isEmpty()) {
                    filteredList.addAll(items)
                } else {
                    for (item in items) {
                        if (item.itemName.toLowerCase(Locale.ROOT).contains(query)) {
                            filteredList.add(item)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = results?.values as List<SalesItem>
                notifyDataSetChanged()
            }
        }
    }
    inner class SalesItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameTextView)
        private val itemPriceTextView: TextView = itemView.findViewById(R.id.itemPriceTextView)
        private val itemQuantityTextView: TextView = itemView.findViewById(R.id.itemQuantityTextView)
        private val itemImageView: ImageView = itemView.findViewById(R.id.itemImageView)
        private val addToCartButton: ImageButton = itemView.findViewById(R.id.addToCartButton)

        fun bind(item: SalesItem) {
            itemNameTextView.text = item.itemName
            itemPriceTextView.text = "Price: ₱${item.itemPrice}"
            itemQuantityTextView.text = "Quantity: ${item.itemQuantity}"
            Picasso.get().load(item.imageURL)
                .placeholder(R.drawable.logo1)
                .resize(150, 150)
                .centerCrop()
                .into(itemImageView)

            // Set onClickListener for addToCartButton
            addToCartButton.setOnClickListener {
                // Check if the item quantity is greater than 0 before decreasing it
                if (item.itemQuantity > 0) {
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
