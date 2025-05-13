package com.example.ecommerce.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
    val id: String = "",
    val userId: String ="",
    val sellerId: String ="",
    val productId: String ="",
    val productName: String ="",
    val productImage: String = "",
    val unitPrice: Double = 0.0,
    var quantity: Int = 0,
    val selectedOptions: List<String> = emptyList()
) : Parcelable
