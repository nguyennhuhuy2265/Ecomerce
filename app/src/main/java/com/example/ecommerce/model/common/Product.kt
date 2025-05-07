package com.example.ecommerce.model.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val categoryId: String = "",
    val categoryName: String? = null,
    val sellerId: String = "",
    val sellerName: String? = null,
    val price: Int = 0,
    val stock: Int = 0,
    val soldCount: Int = 0,
    val avgRating: Double = 0.0,
    val reviewCount: Int = 0,
    val image_public_ids: List<String> = listOf(), // Thay imageUrls bằng danh sách public_ids
    val default_image_public_id: String? = null, // Thay defaultImageUrl bằng public_id
    val optionGroups: List<OptionGroup> = emptyList(),
    val shopLocation: String? = null,
    val createdAt: com.google.firebase.Timestamp? = null,
    val updatedAt: com.google.firebase.Timestamp? = null
) : Parcelable

@Parcelize
data class OptionGroup(
    val id: String = "",
    val name: String = "",
    val values: List<OptionValue> = emptyList(),
    val isRequired: Boolean = false,
    val priority: Int = 0
) : Parcelable

@Parcelize
data class OptionValue(
    val id: String = "",
    val displayName: String = "",
    val extraPrice: Double = 0.0,
    val isDefault: Boolean = false,
    val stock: Int = 0
) : Parcelable