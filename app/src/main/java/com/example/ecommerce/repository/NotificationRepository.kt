package com.example.ecommerce.repository

import com.example.ecommerce.model.Notification
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID

class NotificationRepository {
    private val db = FirebaseFirestore.getInstance()
    private val notificationsCollection = db.collection("notifications")

    suspend fun sendNotification(userId: String, title: String, body: String, orderId: String? = null) {
        try {
            val notification = Notification(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = title,
                body = body,
                createdAt = Timestamp.now(),
                isRead = false,
                orderId = orderId // Lưu orderId vào thông báo
            )
            notificationsCollection.document(notification.id).set(notification).await()
        } catch (e: Exception) {
            // Có thể log lỗi nếu cần
        }
    }
}