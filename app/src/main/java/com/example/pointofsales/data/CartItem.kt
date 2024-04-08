package com.example.pointofsales.data


import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartItem(
    val itemName: String,
    val itemPrice: String,
    var quantity: Int,
    val imageURL: String
) : Parcelable
