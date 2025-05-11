package com.example.ecommerce.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
    val id: String,
    val userId: String,
    val items: List<CartItem> = emptyList(),
    val updatedAt: Timestamp? = null
) : Parcelable

@Parcelize
data class CartItem(
    val id: String,
    val userId: String,
    val productId: String,
    val productName: String,
    val productImage: String,
    val unitPrice: Double,
    var quantity: Int,
    val selectedOptions: List<String> = emptyList()
) : Parcelable
