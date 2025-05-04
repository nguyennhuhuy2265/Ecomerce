package com.example.ecommerce.model.common

data class CartItem(
    val id: String,
    val productId: String,
    val productName: String,
    val productImage: String,
    val unitPrice: Double,
    var quantity: Int
)
