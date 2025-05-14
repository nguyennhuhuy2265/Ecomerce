package com.example.ecommerce.viewmodel.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.model.PaymentStatus
import com.example.ecommerce.repository.NotificationRepository
import com.example.ecommerce.repository.OrderRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OrderDetailViewModel : ViewModel() {
    private val orderRepository = OrderRepository()
    private val notificationRepository = NotificationRepository()

    private val _order = MutableLiveData<Order>()
    val order: LiveData<Order> get() = _order

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _updateResult = MutableLiveData<Boolean>()
    val updateResult: LiveData<Boolean> get() = _updateResult

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.getOrderById(orderId).onSuccess { order ->
                _order.value = order
            }.onFailure { e ->
                _error.value = e.message ?: "Lỗi khi tải chi tiết đơn hàng"
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            _order.value?.let { currentOrder ->
                val updatedOrder = currentOrder.copy(
                    status = newStatus,
                    updatedAt = Timestamp.now()
                )
                orderRepository.updateOrder(updatedOrder).onSuccess {
                    _order.value = updatedOrder
                    _updateResult.value = true

                    // Nếu trạng thái là SHIPPING, chờ một thời gian rồi chuyển sang DELIVERED
                    if (newStatus == OrderStatus.SHIPPING) {
                        // Chờ 5 giây (trong thực tế có thể là 1 ngày: 24 * 60 * 60 * 1000L)
                        delay(5000L)

                        val deliveredOrder = updatedOrder.copy(
                            status = OrderStatus.DELIVERED,
                            paymentStatus = PaymentStatus.PAID,
                            updatedAt = Timestamp.now()
                        )
                        orderRepository.updateOrder(deliveredOrder).onSuccess {
                            _order.value = deliveredOrder
                            _updateResult.value = true

                            // Gửi thông báo "Đã giao hàng" cho user
                            notificationRepository.sendNotification(
                                userId = deliveredOrder.userId,
                                title = "Đơn hàng đã được giao",
                                body = "Đơn hàng #${deliveredOrder.id} đã được giao thành công, hãy kiểm tra thông tin đơn hàng.",
                                orderId = deliveredOrder.id
                            )
                            // Gửi thông báo "Đã giao hàng" cho seller
                            notificationRepository.sendNotification(
                                userId = deliveredOrder.sellerId,
                                title = "Đơn hàng đã được giao",
                                body = "Đơn hàng #${deliveredOrder.id} đã được giao thành công cho khách hàng, hãy hỗ trợ khách hàng nếu cần.",
                                orderId = deliveredOrder.id
                            )
                        }.onFailure { e ->
                            _error.value = e.message ?: "Lỗi khi cập nhật trạng thái thành DELIVERED"
                        }
                    }
                }.onFailure { e ->
                    _error.value = e.message ?: "Lỗi khi cập nhật trạng thái"
                }
            }
        }
    }
}