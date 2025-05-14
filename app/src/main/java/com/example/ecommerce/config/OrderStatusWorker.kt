package com.example.ecommerce.config

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.model.PaymentStatus
import com.example.ecommerce.repository.NotificationRepository
import com.example.ecommerce.repository.OrderRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.tasks.await

class OrderStatusWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val orderRepository = OrderRepository()
    private val notificationRepository = NotificationRepository()

    override suspend fun doWork(): Result {
        val orderId = inputData.getString("orderId") ?: return Result.failure()

        return try {
            // Lấy đơn hàng từ Firestore
            val orderResult = orderRepository.getOrderById(orderId)
            val order = orderResult.getOrNull() ?: return Result.failure()

            // Kiểm tra nếu trạng thái vẫn là SHIPPING
            if (order.status == OrderStatus.SHIPPING) {
                // Cập nhật trạng thái thành DELIVERED
                val deliveredOrder = order.copy(
                    status = OrderStatus.DELIVERED,
                    paymentStatus = PaymentStatus.PAID,
                    updatedAt = Timestamp.now()
                )
                orderRepository.updateOrder(deliveredOrder).onSuccess {
                    // Gửi thông báo cho user
                    notificationRepository.sendNotification(
                        userId = deliveredOrder.userId,
                        title = "Đơn hàng đã được giao",
                        body = "Đơn hàng #${deliveredOrder.id} đã được giao thành công, hãy kiểm tra thông tin đơn hàng.",
                        orderId = deliveredOrder.id
                    )
                    // Gửi thông báo cho seller
                    notificationRepository.sendNotification(
                        userId = deliveredOrder.sellerId,
                        title = "Đơn hàng đã được giao",
                        body = "Đơn hàng #${deliveredOrder.id} đã được giao thành công cho khách hàng, hãy hỗ trợ khách hàng nếu cần.",
                        orderId = deliveredOrder.id
                    )
                }.onFailure {
                    return Result.retry() // Thử lại nếu thất bại
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry() // Thử lại nếu có lỗi
        }
    }
}