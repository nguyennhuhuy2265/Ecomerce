package com.example.ecommerce.model

import com.google.firebase.Timestamp

data class Notification(
    val id: String,
    val userId: String, // Liên kết với người dùng
    val title: String,
    val body: String,
    val createdAt: Timestamp, // Thay timestamp bằng createdAt
    var isRead: Boolean = false, // Cho phép thay đổi trạng thái
    val updatedAt: Timestamp? = null, // Theo dõi cập nhật (tùy chọn)
    val type: String? = null // Loại thông báo (tùy chọn)
)