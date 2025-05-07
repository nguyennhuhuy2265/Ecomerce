package com.example.ecommerce.model.common

data class Banner(
    val id: String = "",
    val image_public_id: String = "", // Thay imageUrl bằng public_id
    val createdAt: com.google.firebase.Timestamp? = null
)