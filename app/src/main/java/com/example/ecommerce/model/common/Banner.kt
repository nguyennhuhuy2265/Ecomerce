package com.example.ecommerce.model.common

data class Banner(
    val id: String = "",
    val imageUrl: String = "",
    val createdAt: com.google.firebase.Timestamp? = null
)