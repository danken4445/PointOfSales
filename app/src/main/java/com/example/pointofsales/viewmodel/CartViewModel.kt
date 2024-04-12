package com.example.pointofsales.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pointofsales.data.CartItem
import com.example.pointofsales.data.SalesItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*

class CartViewModel : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> get() = _cartItems

    private var orderIdCounter = 0

    // Firebase
    private val databaseRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Items")
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ordersRef: DatabaseReference = database.getReference("POSorders")
    private val itemsRef: DatabaseReference = database.getReference("Items")
    private val orderCounterRef: DatabaseReference = database.getReference("OrderCounter")

    init {
        // Initialize order ID counter on first run
        getOrderIdCounter()
    }


    // Function to clear the cart
    fun clearCart() {
        _cartItems.value = emptyList()
    }

    // Function to get the next order ID
    fun getNextOrderId(): Int {
        val orderId = orderIdCounter
        incrementOrderIdCounter()
        return orderId
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
            currentItems.add(CartItem(item.itemName, item.itemPrice, 1, item.imageURL))
        }

        // Update _cartItems LiveData with the new list of cart items
        _cartItems.value = currentItems
    }

    // Function to increase item quantity in cart
    fun increaseQuantity(item: CartItem) {
        val currentItems = _cartItems.value.orEmpty().toMutableList()
        val existingItem = currentItems.find { it.itemName == item.itemName }
        if (existingItem != null) {
            val itemName = existingItem.itemName // Get the item name
            getItemKeyFromDatabase(itemName) { itemKey ->
                if (itemKey == null) {
                    return@getItemKeyFromDatabase
                }

                getCurrentItemQuantity(itemKey) { currentQuantity ->
                    val newQuantity = existingItem.quantity + 1
                    if (newQuantity <= currentQuantity) {
                        existingItem.quantity = newQuantity
                        _cartItems.value = currentItems
                        updateItemQuantityInDatabase(itemKey, newQuantity)
                    } else {

                    }
                }
            }
        }
    }



    // Function to decrease item quantity in cart
    fun decreaseQuantity(item: CartItem) {
        val currentItems = _cartItems.value.orEmpty().toMutableList()
        val existingItem = currentItems.find { it.itemName == item.itemName }
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                existingItem.quantity--
                _cartItems.value = currentItems
            } else {
                currentItems.remove(existingItem)
                _cartItems.value = currentItems
            }
        }
    }


    // Function to send order to Firebase
    fun sendOrderToFirebase(context: Context, inventoryViewModel: InventoryViewModel, paymentMethod: String, referenceNumber: String) {
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
        orderMap["paymentMethod"] = paymentMethod
        orderMap["referenceNumber"] = referenceNumber

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
                // Update item quantities under "Items" node
                cartItems.forEach { item ->
                    itemsRef.orderByChild("itemName").equalTo(item.itemName).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (itemSnapshot in snapshot.children) {
                                    val itemKey = itemSnapshot.key
                                    val currentQuantity = itemSnapshot.child("itemQuantity").getValue(Int::class.java) ?: 0
                                    val updatedQuantity = currentQuantity - item.quantity
                                    itemsRef.child(itemKey!!).child("itemQuantity").setValue(updatedQuantity)
                                        .addOnSuccessListener {
                                            Log.d("CartViewModel", "Item quantity updated successfully")
                                        }
                                        .addOnFailureListener { e ->
                                            Log.e("CartViewModel", "Failed to update item quantity: ${e.message}")
                                        }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.e("CartViewModel", "Failed to retrieve item key: ${error.message}")
                        }
                    })
                }

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

    fun updateQuantity(item: CartItem, newQuantity: Int) {
        val currentItems = _cartItems.value.orEmpty().toMutableList()
        val existingItem = currentItems.find { it.itemName == item.itemName }
        if (existingItem != null) {
            existingItem.quantity = newQuantity
            _cartItems.value = currentItems
        }
    }

    // Function to update item quantity in the database
    private fun updateItemQuantityInDatabase(itemName: String, newQuantity: Int) {
        databaseRef.orderByChild("itemName").equalTo(itemName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (itemSnapshot in snapshot.children) {
                        val itemKey = itemSnapshot.key
                        databaseRef.child(itemKey!!).child("itemQuantity").setValue(newQuantity)
                            .addOnSuccessListener {
                                Log.d("CartViewModel", "Item quantity updated successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.e("CartViewModel", "Failed to update item quantity: ${e.message}")
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CartViewModel", "Failed to retrieve item key: ${error.message}")
            }
        })
    }
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
    // Function to get the order ID counter from the database
    private fun getOrderIdCounter() {
        orderCounterRef.child("orderIdCounter").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderIdCounter = snapshot.getValue(Int::class.java) ?: 0
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CartViewModel", "Failed to retrieve order ID counter: ${error.message}")
            }
        })
    }

    // Function to set the order ID counter in the database
    private fun setOrderIdCounter(counter: Int) {
        orderCounterRef.child("orderIdCounter").setValue(counter)
            .addOnSuccessListener {
                orderIdCounter = counter
            }
            .addOnFailureListener { e ->
                Log.e("CartViewModel", "Failed to set order ID counter: ${e.message}")
            }
    }

    // Function to increment the order ID counter in the database
    private fun incrementOrderIdCounter() {
        orderIdCounter++
        setOrderIdCounter(orderIdCounter)
    }
}




