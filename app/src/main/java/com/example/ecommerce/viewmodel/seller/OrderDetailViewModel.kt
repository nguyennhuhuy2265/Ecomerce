package com.example.ecommerce.viewmodel.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.ecommerce.config.OrderStatusWorker
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.repository.OrderRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class OrderDetailViewModel : ViewModel() {
    private val orderRepository = OrderRepository()

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

                    // Nếu trạng thái là SHIPPING, lên lịch Worker để chuyển sang DELIVERED
                    if (newStatus == OrderStatus.SHIPPING) {
                        val workData = Data.Builder()
                            .putString("orderId", orderId)
                            .build()

                        // Lên lịch Worker chạy sau 5 giây (trong thực tế có thể là 1 ngày)
                        val workRequest = OneTimeWorkRequestBuilder<OrderStatusWorker>()
                            .setInputData(workData)
                            .setInitialDelay(10, TimeUnit.SECONDS) // 5 giây để demo, có thể đổi thành 1 ngày
                            .build()

                        WorkManager.getInstance()
                            .enqueue(workRequest)
                    }
                }.onFailure { e ->
                    _error.value = e.message ?: "Lỗi khi cập nhật trạng thái"
                }
            }
        }
    }
}