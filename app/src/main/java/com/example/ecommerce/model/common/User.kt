package com.example.ecommerce.model.common

data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val phone: String?,
    val address: String?,
    val joinedAt: Long       // timestamp khi đăng ký
)
