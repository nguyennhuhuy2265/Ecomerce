package com.example.ecommerce.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Cart(
    val id: String,
    val userId: String, // Thêm userId để liên kết với người dùng
    val productId: String,
    val productName: String,
    val productImage: String, // URL trực tiếp
    val unitPrice: Double,
    var quantity: Int,
    val selectedOptions: List<OptionValue> = emptyList() // Thêm nếu có tùy chọn
) : Parcelable
