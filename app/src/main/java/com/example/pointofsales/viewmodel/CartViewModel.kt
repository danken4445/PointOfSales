package com.example.pointofsales.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pointofsales.data.CartItem
import com.example.pointofsales.data.SalesItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class CartViewModel : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    private var orderIdCounter = 0

    // Firebase
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ordersRef: DatabaseReference = database.getReference("POSorders")

    // Function to clear the cart
    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // Function to get the next order ID
    fun getNextOrderId(): Int {
        return orderIdCounter++
    }

    // Function to calculate total price
    fun calculateTotalPrice(): Double {
        var totalPrice = 0.0
        val cartItems = _cartItems.value.orEmpty()
        for (item in cartItems) {
            totalPrice += item.itemPrice.toDouble() * item.quantity
        }
        return totalPrice
    }

    // Function to add item to cart
    fun addItemToCart(item: SalesItem) {
        // Fetch the current cart items
        val currentItems = _cartItems.value.orEmpty().toMutableList()

        // Check if the item is already in the cart
        val existingItem = currentItems.find { it.itemName == item.itemName }

        if (existingItem != null) {
            // If the item already exists, increase its quantity
            existingItem.quantity++
        } else {
            // If the item does not exist, add it to the cart with quantity 1
            currentItems.add(CartItem(item.itemName, item.itemPrice, 1))
        }

        // Update _cartItems LiveData with the new list of cart items
        _cartItems.value = currentItems
    }

    // Function to send order to Firebase
    fun sendOrderToFirebase(context: Context) {
        // Get current date and time
        val currentDateTime = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dateTimeString = dateFormat.format(currentDateTime)

        // Generate order ID
        val orderId = getNextOrderId()

        // Get cart items
        val cartItems = _cartItems.value.orEmpty()

        // Calculate total price
        val totalPrice = calculateTotalPrice()

        // Prepare order details
        val orderMap = HashMap<String, Any>()
        orderMap["orderID"] = orderId
        orderMap["dateAndTime"] = dateTimeString
        orderMap["totalPrice"] = totalPrice

        // Prepare Products node
        val productsMap = HashMap<String, Any>()

        // Add each item to Products node
        cartItems.forEachIndexed { index, item ->
            val productDetails = HashMap<String, Any>()
            productDetails["itemName"] = item.itemName
            productDetails["itemPrice"] = item.itemPrice
            productDetails["itemQuantity"] = item.quantity
            productsMap["product$index"] = productDetails
        }

        orderMap["Products"] = productsMap

        // Push order details to Firebase
        ordersRef.child(orderId.toString()).setValue(orderMap)
            .addOnSuccessListener {
                // Clear the cart after checkout
                clearCart()

                // Show confirmation toast with orderId
                Toast.makeText(context, "Order $orderId has been placed", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Handle any errors
                Log.e("CartViewModel", "Error sending order to Firebase: ${e.message}")
                Toast.makeText(context, "Failed to place order", Toast.LENGTH_SHORT).show()
            }
    }
}
