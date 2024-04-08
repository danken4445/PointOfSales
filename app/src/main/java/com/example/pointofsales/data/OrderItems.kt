package com.example.pointofsales.data

data class OrderItems(
    val orderId: Long,
    val dateTime: String,
    val totalPrice: Double,
    val products: List<InventoryItem>
)
