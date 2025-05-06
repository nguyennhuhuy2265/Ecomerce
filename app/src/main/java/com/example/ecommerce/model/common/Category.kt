package com.example.ecommerce.model.common

data class Category(
    val id: String = "", // DocumentID thủ công (ví dụ: "electronics")
    val name: String = "",
    val description: String? = null,
    val imageUrl: String? = null,
    val priority: Int = 0,
    val createdAt: com.google.firebase.Timestamp? = null,
    val updatedAt: com.google.firebase.Timestamp? = null
)