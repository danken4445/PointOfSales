package com.example.pointofsales.adapter
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import com.example.pointofsales.R
import com.example.pointofsales.data.CartItem
import com.example.pointofsales.viewmodel.CartViewModel
import android.text.InputFilter
import android.text.InputType
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.util.Locale


class CartAdapter(private val context: Context, private val viewModel: CartViewModel) : RecyclerView.Adapter<CartAdapter.CartItemViewHolder>() {

    private var items: MutableList<CartItem> = mutableListOf()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val itemsRef: DatabaseReference = database.getReference("Items")


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

    fun calculateTotalPrice(): Double {
        var totalPrice = 0.0
        for (item in items) {
            val price = item.itemPrice.toDoubleOrNull() ?: 0.0
            totalPrice += price * item.quantity
        }
        return totalPrice
    }

    fun getItems(): List<CartItem> {
        return items
    }

    inner class CartItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemNameTextView: TextView = itemView.findViewById(R.id.itemNameCart)
        private val itemPriceTextView: TextView = itemView.findViewById(R.id.itemPriceCart)
        private val itemQuantityTextView: TextView =
            itemView.findViewById(R.id.itemQuantityTextViewCart)
        private val imageViewCart: ImageView = itemView.findViewById(R.id.imageViewCart)

        private val decreaseQuantityButton: ImageButton =
            itemView.findViewById(R.id.decreaseQuantityButtonCart)
        private val increaseQuantityButton: ImageButton =
            itemView.findViewById(R.id.increaseQuantityButtonCart)

        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "PH"))

        fun bind(item: CartItem) {
            itemNameTextView.text = item.itemName

            // Convert item price to Double and format as currency
            val price = item.itemPrice.toDoubleOrNull() ?: 0.0
            val formattedPrice = currencyFormat.format(price)
            itemPriceTextView.text = formattedPrice

            itemQuantityTextView.text = "Quantity: ${item.quantity}"

            decreaseQuantityButton.setOnClickListener {
                viewModel.decreaseQuantity(item)
                itemQuantityTextView.text = "Quantity: ${item.quantity}" // Update quantity TextView
            }

            increaseQuantityButton.setOnClickListener {
                viewModel.increaseQuantity(item)
                itemQuantityTextView.text = "Quantity: ${item.quantity}" // Update quantity TextView
            }
            itemQuantityTextView.setOnClickListener {
                showQuantityInputDialog(item)
            }

            Picasso.get().load(item.imageURL)
                .placeholder(R.drawable.logo1)
                .resize(150, 150)
                .centerCrop()
                .into(imageViewCart)
        }
    }

    private fun showQuantityInputDialog(item: CartItem) {
        val inputEditText = EditText(context)
        inputEditText.setText(item.quantity.toString())

        // Set input type to number and restrict input to numbers only
        inputEditText.inputType = InputType.TYPE_CLASS_NUMBER

        // Set max length of input to the length of itemQuantity
        inputEditText.filters =
            arrayOf(InputFilter.LengthFilter(3)) // Adjust the maximum length as needed

        getItemKeyFromDatabase(item.itemName) { itemKey ->
            if (itemKey == null) {
                Toast.makeText(context, "Item not found in database", Toast.LENGTH_SHORT).show()
                return@getItemKeyFromDatabase
            }

            AlertDialog.Builder(context)
                .setTitle("Enter Quantity")
                .setView(inputEditText)
                .setPositiveButton("OK") { dialog, _ ->
                    val quantityString = inputEditText.text.toString()
                    if (quantityString.isNotEmpty()) {
                        val newQuantity = quantityString.toInt()
                        getCurrentItemQuantity(itemKey) { currentQuantity ->
                            if (newQuantity <= currentQuantity) {
                                viewModel.updateQuantity(item, newQuantity)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Entered quantity exceeds current quantity",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    // Modified function to get the item key from the database using the itemName
    private fun getItemKeyFromDatabase(itemName: String, callback: (String?) -> Unit) {
        itemsRef.orderByChild("itemName").equalTo(itemName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var itemKey: String? = null
                    if (snapshot.exists()) {
                        for (itemSnapshot in snapshot.children) {
                            itemKey = itemSnapshot.key
                        }
                    }
                    callback(itemKey)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("CartFragment", "Failed to retrieve item key: ${error.message}")
                    callback(null)
                }
            })
    }

    // Function to get the current item quantity from the database
    private fun getCurrentItemQuantity(itemKey: String, callback: (Int) -> Unit) {
        itemsRef.child(itemKey).child("itemQuantity")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentQuantity = snapshot.getValue(Int::class.java) ?: 0
                    callback(currentQuantity)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(
                        "CartFragment",
                        "Failed to retrieve current item quantity: ${error.message}"
                    )
                    callback(0)
                }
            })
    }
}