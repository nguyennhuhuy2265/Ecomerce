package com.example.ecommerce.model

import com.google.firebase.Timestamp

data class Notification(
    val id: String="",
    val userId: String="",
    val title: String="",
    val body: String="",
    val createdAt: Timestamp? = null,
    var read: Boolean = false,
    val updatedAt: Timestamp? = null,
    val type: String? = null,
    val orderId: String? = null // Thêm trường orderId
)