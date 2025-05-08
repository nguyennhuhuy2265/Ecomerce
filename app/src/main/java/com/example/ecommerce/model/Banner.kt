package com.example.ecommerce.model

import com.google.firebase.Timestamp

data class Banner(
    val id: String = "",
    val imageUrl: String = "", // Thay image_public_id thành imageUrl
    val createdAt: Timestamp? = null
)