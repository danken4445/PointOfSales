package com.example.pointofsales.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pointofsales.adapter.InventoryAdapter
import com.example.pointofsales.data.InventoryItem
import com.google.firebase.database.*

class InventoryViewModel : ViewModel() {
    private lateinit var viewModel: InventoryViewModel
    private lateinit var inventoryAdapter: InventoryAdapter // Use InventoryAdapter
    private val database = FirebaseDatabase.getInstance().getReference("Items")

    // Define LiveData for inventory items
    val inventoryItems = MutableLiveData<List<InventoryItem>>()

    // Function to fetch inventory items
    fun fetchInventoryItems() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<InventoryItem>()
                for (itemSnapshot in snapshot.children) {
                    val itemName = itemSnapshot.child("itemName").getValue(String::class.java) ?: ""
                    val itemPrice =
                        itemSnapshot.child("itemPrice").getValue(String::class.java) ?: "0.00"
                    val quantityLong =
                        itemSnapshot.child("itemQuantity").getValue(Int::class.java) ?: 0L
                    val itemQuantity = quantityLong.toInt()

                    // Create InventoryItem with additional imageResource (if applicable)
                    val item = InventoryItem(itemName, itemPrice, itemQuantity)
                    items.add(item)
                }
                inventoryItems.value = items
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

}
