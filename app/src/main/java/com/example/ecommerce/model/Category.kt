package com.example.ecommerce.model

import com.google.firebase.Timestamp

data class Category(
    val id: String = "", // DocumentID thủ công (ví dụ: "electronics")
    val name: String = "",
    val description: String? = null,
    val imageUrl: String = "", // Thay image_public_id thành imageUrl
    val priority: Int = 0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)