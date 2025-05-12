package com.example.ecommerce.model

import com.google.firebase.Timestamp

data class Review(
    val id: String = "",
    val productId: String = "",
    val userId: String = "",
    val userName: String? = null,
    val userAvatarUrl: String? = null,
    val rating: Int = 0,
    val comment: String? = null,
    val createdAt: Timestamp? = null
)