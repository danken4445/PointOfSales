package com.example.pointofsales.viewmodel

import com.example.pointofsales.adapter.InventoryAdapter
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pointofsales.data.InventoryItem
import com.google.firebase.database.*

class InventoryViewModel : ViewModel() {
    private lateinit var viewModel: InventoryViewModel
    private lateinit var inventoryAdapter: InventoryAdapter // Use com.example.pointofsales.adapter.InventoryAdapter
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
                    val imageURL = itemSnapshot.child("imageResource").getValue(String::class.java)


                    // Create InventoryItem with additional imageResource (if applicable)
                    val item = imageURL?.let {
                        InventoryItem(itemName, itemPrice, itemQuantity,
                            it
                        )
                    }
                    if (item != null) {
                        items.add(item)
                    }
                }
                inventoryItems.value = items
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
    fun getDatabaseReference(): DatabaseReference {
        return database
    }
    fun updateItemQuantity(itemId: String, newQuantity: Int) {
        database.orderByChild("itemName").equalTo(itemId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (itemSnapshot in snapshot.children) {
                        val itemKey = itemSnapshot.key
                        database.child(itemKey!!).child("itemQuantity").setValue(newQuantity)
                            .addOnSuccessListener {
                                Log.d("InventoryViewModel", "Item quantity updated successfully")
                            }
                            .addOnFailureListener { e ->
                                Log.e("InventoryViewModel", "Failed to update item quantity: ${e.message}")
                            }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("InventoryViewModel", "Failed to retrieve item key: ${error.message}")
            }
        })
    }
}


