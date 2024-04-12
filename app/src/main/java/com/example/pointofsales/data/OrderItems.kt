package com.example.pointofsales.data

data class OrderItems(
    val orderId: Long,
    val dateTime: String,
    val totalPrice: Double,
    val paymentMethod: String,
    val referenceNumber: String,
    val products: List<InventoryItem>
)
