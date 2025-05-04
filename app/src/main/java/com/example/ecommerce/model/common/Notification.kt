package com.example.ecommerce.model.common

data class Notification(
    val id: String,
    val title: String,
    val body: String,
    val timestamp: Long,
    val isRead: Boolean = false
)
