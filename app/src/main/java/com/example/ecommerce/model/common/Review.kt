package com.example.ecommerce.model.common

data class Review(
    val id: String,
    val productId: String,
    val userId: String,
    val rating: Float,
    val comment: String?,
    val createdAt: Long
)
