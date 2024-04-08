package com.example.pointofsales.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.pointofsales.data.SalesItem
import com.google.firebase.database.*

class SalesViewModel : ViewModel() {

    private val _salesItems = MutableLiveData<List<SalesItem>>()
    val salesItems: LiveData<List<SalesItem>> = _salesItems

    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("Items")

    init {
        fetchSalesItems()
    }

    fun fetchSalesItems() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val salesItemList = mutableListOf<SalesItem>()
                for (itemSnapshot in snapshot.children) {
                    val itemName = itemSnapshot.child("itemName").getValue(String::class.java)
                    val itemPrice = itemSnapshot.child("itemPrice").getValue(String::class.java)
                    val itemQuantity = itemSnapshot.child("itemQuantity").getValue(Int::class.java)
                    val imageURL = itemSnapshot.child("imageResource").getValue(String::class.java)

                    if (itemName != null && itemPrice != null && itemQuantity != null && imageURL != null) {
                        val salesItem = SalesItem(itemName, itemPrice, itemQuantity, imageURL)
                        salesItemList.add(salesItem)
                    }
                }
                _salesItems.value = salesItemList
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

}
