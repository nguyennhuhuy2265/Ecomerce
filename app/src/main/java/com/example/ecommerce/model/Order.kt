package com.example.ecommerce.model

import com.google.firebase.Timestamp

data class Order(
    val id: String,
    val userId: String,
    val sellerId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val status: OrderStatus,
    val shippingAddress: Address? = null, // Thêm địa chỉ giao hàng
    val createdAt: Timestamp, // Đổi thành Timestamp
    val updatedAt: Timestamp? = null // Thêm để theo dõi cập nhật
)

data class OrderItem(
    val id: String,
    val productId: String,
    val productName: String,
    val productImage: String, // URL trực tiếp
    val unitPrice: Double,
    val quantity: Int,
    val selectedOptions: List<String> = emptyList() // Thêm nếu có tùy chọn
)
enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELED
}