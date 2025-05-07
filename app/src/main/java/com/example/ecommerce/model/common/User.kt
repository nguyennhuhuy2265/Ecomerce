package com.example.ecommerce.model.common

data class Address(
    val streetNumber: String? = null,
    val streetName: String? = null,
    val ward: String? = null,
    val district: String? = null,
    val city: String? = null,
    val country: String? = null
)

data class User(
    val email: String = "",
    val name: String = "",
    val role: String = "user", // "user" hoặc "seller"
    val address: Address? = null,
    val avatar_public_id: String? = null, // Thay avatarUrl bằng public_id
    val shopCategory: String? = null, // Chỉ có nếu role = "seller"
    val shopName: String? = null, // Chỉ có nếu role = "seller"
    val recommendedProductIds: List<String> = emptyList(), // Chỉ có nếu role = "user"
    val isActive: Boolean = true, // Chỉ có nếu role = "seller"
    val phoneNumber: String? = null, // Chỉ có nếu role = "seller"
    val rating: Double = 0.0, // Chỉ có nếu role = "seller"
    val reviewCount: Int = 0, // Chỉ có nếu role = "seller"
    val isVerified: Boolean = false, // Chỉ có nếu role = "seller"
    val businessHours: Map<String, String> = emptyMap(), // Chỉ có nếu role = "seller"
    val banner_public_id: String? = null, // Thay bannerUrl bằng public_id (cho seller)
    val followersCount: Int = 0, // Chỉ có nếu role = "seller"
    val createdAt: com.google.firebase.Timestamp? = null,
    val updatedAt: com.google.firebase.Timestamp? = null
)