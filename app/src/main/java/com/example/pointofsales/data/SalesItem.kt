package com.example.pointofsales.data


data class SalesItem(
    val itemName: String,
    val itemPrice: String,
    var itemQuantity: Int,
    var imageURL: String
)
