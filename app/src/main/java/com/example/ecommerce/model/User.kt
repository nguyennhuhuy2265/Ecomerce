package com.example.ecommerce.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val phoneNumber: String? = null,
    val streetNumber: String? = null,
    val streetName: String? = null,
    val ward: String? = null,
    val district: String? = null,
    val city: String? = null,
    val country: String? = null
) : Parcelable

@Parcelize
data class Card(
    val bankName: String = "", // Tên ngân hàng
    val cardNumber: String = "", // Số thẻ
    val cardHolderName: String = "", // Tên chủ thẻ
    val expiryDate: String = "", // Ngày hết hạn (định dạng MM/YY)
    val cvv: String = "" // Mã CVV
) : Parcelable

@Parcelize
data class User(
    var id: String = "",
    val email: String = "",
    val name: String = "",
    val role: String = "user", // "user" hoặc "seller"
    val address: Address? = null,
    val avatarUrl: String? = null,
    val shopCategory: String? = null, // Chỉ có nếu role = "seller"
    val shopName: String? = null, // Chỉ có nếu role = "seller"
    val phoneNumber: String? = null, // Cả user và seller có thể có hoặc không
    val rating: Double = 0.0, // Chỉ có nếu role = "seller"
    val reviewCount: Int = 0, // Chỉ có nếu role = "seller"
    val revenue: Double = 0.0,
    val soldCount: Int = 0,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null,
    val card: Card? = null // Một thẻ duy nhất, có thể null
) : Parcelable