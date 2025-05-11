package com.example.ecommerce.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val categoryId: String = "",
    val sellerId: String = "",
    val price: Int = 0,
    val stock: Int = 0,
    val soldCount: Int = 0,
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val imageUrls: List<String> = listOf(),
    val optionGroups: List<OptionGroup> = emptyList(),
    val shopLocation: String? = null,
    val createdAt: Timestamp? = null
) : Parcelable

@Parcelize
data class OptionGroup(
    val id: String = "",
    val name: String = "",
    val values: List<String> = emptyList()
) : Parcelable

