package com.example.ecommerce.repository

import com.example.ecommerce.model.Order
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class OrderRepository {
    private val db = FirebaseFirestore.getInstance()
    private val ordersCollection = db.collection("orders")

    suspend fun createOrder(userId: String, order: Order): Result<String> {
        return try {
            val newOrder = order.copy(
                id = "${UUID.randomUUID()}"
            )
            ordersCollection.document(newOrder.id).set(newOrder).await()
            Result.success(newOrder.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrdersByUserId(userId: String): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection.whereEqualTo("userId", userId).get().await()
            val orders = snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrdersBySeller(sellerId: String): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection.whereEqualTo("sellerId", sellerId).get().await()
            val orders = snapshot.documents.mapNotNull { it.toObject(Order::class.java) }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrderById(orderId: String): Result<Order> {
        return try {
            val snapshot = ordersCollection.document(orderId).get().await()
            val order = snapshot.toObject(Order::class.java)
            if (order != null) {
                Result.success(order)
            } else {
                Result.failure(Exception("Đơn hàng không tồn tại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrder(order: Order): Result<Unit> {
        return try {
            ordersCollection.document(order.id).set(order).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}