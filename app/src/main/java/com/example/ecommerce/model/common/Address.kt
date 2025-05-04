package com.example.ecommerce.model.common

data class Address(
    val id: String,
    val userId: String,
    val recipientName: String,
    val phone: String,
    val street: String,
    val city: String,
    val zipCode: String,
    val isDefault: Boolean = false
)

data class PaymentMethod(
    val id: String,
    val userId: String,
    val type: PaymentType,
    val cardNumber: String?,
    val expiry: String?,
    val isDefault: Boolean = false
)

enum class PaymentType { CREDIT_CARD, BANK_TRANSFER, COD }
