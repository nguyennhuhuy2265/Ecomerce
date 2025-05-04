package com.example.ecommerce.model.common

data class OrderItem(
    val productId: String,
    val productName: String,
    val productImage: String,
    val unitPrice: Double,
    val quantity: Int
)

data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val status: OrderStatus,
    val createdAt: Long
)

enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELED
}
