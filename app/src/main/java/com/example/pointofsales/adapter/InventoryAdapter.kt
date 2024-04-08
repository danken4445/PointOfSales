package com.example.pointofsales.adapter

import android.app.AlertDialog
import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.pointofsales.R
import com.example.pointofsales.data.InventoryItem
import com.example.pointofsales.viewmodel.InventoryViewModel
import com.squareup.picasso.Picasso
import java.util.Locale

class InventoryAdapter(
    private val context: Context,
    private val inventoryViewModel: InventoryViewModel
) : RecyclerView.Adapter<InventoryAdapter.InventoryItemViewHolder>(), Filterable {

    private var items: List<InventoryItem> = listOf()
    private var filteredItems: List<InventoryItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_inventory, parent, false)
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
        private val itemQuantityTextView: TextView =
            itemView.findViewById(R.id.itemQuantityTextView)
        private val decreaseQuantityButton: ImageButton =
            itemView.findViewById(R.id.decreaseQuantityButton)
        private val increaseQuantityButton: ImageButton =
            itemView.findViewById(R.id.increaseQuantityButton)
        private val itemImageView: ImageView = itemView.findViewById(R.id.itemImageView)

        init {
            // Set onClickListener for itemQuantityTextView to show quantity input dialog
            itemQuantityTextView.setOnClickListener {
                val item = items[adapterPosition]
                showQuantityInputDialog(item)
            }
        }

        fun bind(item: InventoryItem) {
            itemNameTextView.text = "${item.itemName}"
            itemPriceTextView.text = "Price: ₱${item.itemPrice}"
            itemQuantityTextView.text = "Quantity: ${item.itemQuantity}"

            Picasso.get().load(item.imageURL)
                .placeholder(R.drawable.logo1)
                .resize(150, 150)
                .centerCrop()
                .into(itemImageView)

            // Decrease quantity button click listener
            decreaseQuantityButton.setOnClickListener {
                decreaseQuantity(item)
            }

            // Increase quantity button click listener
            increaseQuantityButton.setOnClickListener {
                increaseQuantity(item)
            }
        }

        private fun decreaseQuantity(item: InventoryItem) {
            val newQuantity = item.itemQuantity - 1
            if (newQuantity >= 0) {
                inventoryViewModel.updateItemQuantity(item.itemName, newQuantity)
            } else {
                Toast.makeText(context, "Quantity cannot be negative", Toast.LENGTH_SHORT).show()
            }
        }

        private fun increaseQuantity(item: InventoryItem) {
            val newQuantity = item.itemQuantity + 1
            inventoryViewModel.updateItemQuantity(item.itemName, newQuantity)
        }

        private fun showQuantityInputDialog(item: InventoryItem) {
            val inputEditText = EditText(context)
            inputEditText.setText(item.itemQuantity.toString())

            // Set input type to number and restrict input to numbers only
            inputEditText.inputType = InputType.TYPE_CLASS_NUMBER

            // Set max length of input to the length of itemQuantity
            inputEditText.filters =
                arrayOf(InputFilter.LengthFilter(3)) // Adjust the maximum length as needed

            AlertDialog.Builder(context)
                .setTitle("Enter Quantity")
                .setView(inputEditText)
                .setPositiveButton("OK") { dialog, _ ->
                    val quantityString = inputEditText.text.toString()
                    if (quantityString.isNotEmpty()) {
                        val newQuantity = quantityString.toInt()
                        inventoryViewModel.updateItemQuantity(item.itemName, newQuantity)
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<InventoryItem>()
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
                filteredItems = results?.values as List<InventoryItem>
                notifyDataSetChanged()
            }
        }
    }



}


