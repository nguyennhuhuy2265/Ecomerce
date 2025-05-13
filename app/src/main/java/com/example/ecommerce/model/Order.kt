package com.example.ecommerce.model

import com.google.firebase.Timestamp

data class Order(
    val id: String = "", // Giá trị mặc định
    val userId: String = "", // Giá trị mặc định
    val sellerId: String = "", // Giá trị mặc định
    val productId: String = "", // Giá trị mặc định
    val productName: String = "", // Giá trị mặc định
    val productImage: String = "", // Giá trị mặc định
    val unitPrice: Double = 0.0, // Giá trị mặc định
    val quantity: Int = 0, // Giá trị mặc định
    val selectedOptions: List<String> = emptyList(),
    val totalAmount: Double = 0.0, // Giá trị mặc định
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING, // Giá trị mặc định
    val status: OrderStatus = OrderStatus.PENDING, // Giá trị mặc định
    val shippingAddress: Address? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp? = null
)

enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELED
}

enum class PaymentStatus {
    PENDING, PAID
}