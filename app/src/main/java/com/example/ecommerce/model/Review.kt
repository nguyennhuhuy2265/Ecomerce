package com.example.ecommerce.model

import java.sql.Timestamp

data class Review(
    val id: String,
    val productId: String,
    val userId: String,
    val userName: String? = null, // Thêm để hiển thị tên người đánh giá
    val userAvatarUrl: String? = null, // Thêm để hiển thị ảnh đại diện
    val rating: Float,
    val comment: String?,
    val createdAt: Timestamp // Đổi thành Timestamp
)