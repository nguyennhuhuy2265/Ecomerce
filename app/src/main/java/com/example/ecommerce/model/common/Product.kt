package com.example.ecommerce.model.common

data class Product(
    val id: String,
    val name: String,
    val description: String?,
    val imageUrl: String,
    val price: Double,
    val originalPrice: Double?,    // null nếu không có giá gạch chân
    val rating: Float?,
    val soldCount: Int?,
    val categoryId: String,
    val isFeatured: Boolean = false
)
