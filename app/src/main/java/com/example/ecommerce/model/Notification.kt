package com.example.ecommerce.model

import com.google.firebase.Timestamp

data class Notification(
    val id: String,
    val userId: String, // Thêm userId để liên kết với người dùng
    val title: String,
    val body: String,
    val timestamp: Timestamp, // Đổi thành Timestamp
    val isRead: Boolean = false
)