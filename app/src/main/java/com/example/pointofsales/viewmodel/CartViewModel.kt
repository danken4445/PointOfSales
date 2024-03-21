package com.example.pointofsales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pointofsales.data.CartItem
import com.example.pointofsales.data.SalesItem

class CartViewModel : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

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

}
