package com.example.pointofsales.repository

import com.example.pointofsales.data.InventoryItem
import com.google.firebase.database.*

class InventoryRepository {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("InventoryItems")

    fun fetchInventoryItems(callback: (List<InventoryItem>) -> Unit) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<InventoryItem>()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(InventoryItem::class.java)
                    item?.let { items.add(it) }
                }
                callback(items)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}
