package com.example.pointofsales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pointofsales.data.InventoryItem
import com.example.pointofsales.data.OrderItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().getReference("POSorders")

    private val _orders = MutableLiveData<List<OrderItems>>()
    val orders: LiveData<List<OrderItems>> get() = _orders

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> get() = _totalPrice

    init {
        fetchOrders()
    }

    fun fetchOrders() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ordersList = mutableListOf<OrderItems>()
                var totalPrice = 0.0 // Initialize total price

                for (orderSnapshot in snapshot.children) {
                    val orderId = orderSnapshot.child("orderID").getValue(Long::class.java) ?: 0L
                    val dateTime = orderSnapshot.child("dateAndTime").getValue(String::class.java) ?: ""
                    val orderTotalPrice = orderSnapshot.child("totalPrice").getValue(Double::class.java) ?: 0.0
                    val paymentMethod = orderSnapshot.child("paymentMethod").getValue(String::class.java) ?: ""
                    val referenceNo = orderSnapshot.child("referenceNumber").getValue(String::class.java) ?: ""



                    // Accumulate total price
                    totalPrice += orderTotalPrice

                    val productsList = mutableListOf<InventoryItem>()
                    val productsSnapshot = orderSnapshot.child("Products")
                    for (productSnapshot in productsSnapshot.children) {
                        val itemName = productSnapshot.child("itemName").getValue(String::class.java) ?: ""
                        val itemPrice = productSnapshot.child("itemPrice").getValue(String::class.java) ?: ""
                        val itemQuantity = productSnapshot.child("itemQuantity").getValue(Int::class.java) ?: 0
                        val imageURL = productSnapshot.child("imageResource").getValue(String::class.java) ?: ""

                        val product = InventoryItem(itemName, itemPrice, itemQuantity, imageURL)
                        productsList.add(product)
                    }

                    val order = OrderItems(orderId, dateTime, orderTotalPrice, paymentMethod, referenceNo, productsList)
                    ordersList.add(order)
                }

                // Update LiveData with orders and total price
                _orders.value = ordersList
                _totalPrice.value = totalPrice
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}
