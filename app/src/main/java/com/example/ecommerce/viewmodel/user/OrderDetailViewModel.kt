package com.example.ecommerce.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.model.User
import com.example.ecommerce.repository.OrderRepository
import com.example.ecommerce.repository.ReviewRepository
import com.example.ecommerce.repository.common.UserRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class OrderDetailViewModel : ViewModel() {

    private val orderRepository = OrderRepository()
    private val userRepository = UserRepository()
    private val reviewRepository = ReviewRepository()

    private val _order = MutableLiveData<Order?>()
    val order: LiveData<Order?> get() = _order

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _hasReviewed = MutableLiveData<Boolean>()
    val hasReviewed: LiveData<Boolean> get() = _hasReviewed

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.getOrderById(orderId).onSuccess { order ->
                _order.value = order
                // Load thông tin người dùng để lấy tên người nhận
                order?.userId?.let { userId ->
                    userRepository.getUserById(userId).onSuccess { user ->
                        _user.value = user
                        // Kiểm tra xem người dùng đã đánh giá sản phẩm này chưa
                        checkIfUserHasReviewed(order)
                    }.onFailure { e ->
                        _error.value = e.message ?: "Lỗi khi tải thông tin người dùng"
                    }
                }
            }.onFailure { e ->
                _order.value = null
                _error.value = e.message ?: "Lỗi khi tải đơn hàng"
            }
        }
    }

    private fun checkIfUserHasReviewed(order: Order) {
        viewModelScope.launch {
            reviewRepository.getReviewsByProductId(order.productId).onSuccess { reviews ->
                val userId = order.userId
                _hasReviewed.value = reviews.any { it.userId == userId }
            }.onFailure { e ->
                _error.value = e.message ?: "Lỗi khi kiểm tra đánh giá"
                _hasReviewed.value = false // Mặc định là chưa đánh giá nếu có lỗi
            }
        }
    }

    fun cancelOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.getOrderById(orderId).onSuccess { order ->
                val updatedOrder = order.copy(
                    status = OrderStatus.CANCELED,
                    updatedAt = Timestamp.now()
                )
                orderRepository.updateOrder(updatedOrder).onSuccess {
                    _order.value = updatedOrder
                }.onFailure { e ->
                    _error.value = e.message ?: "Lỗi khi hủy đơn hàng"
                }
            }.onFailure { e ->
                _error.value = e.message ?: "Lỗi khi tải đơn hàng để hủy"
            }
        }
    }
}