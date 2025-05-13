package com.example.ecommerce.viewmodel.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.model.User
import com.example.ecommerce.repository.OrderRepository
import com.example.ecommerce.repository.common.UserRepository
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val orderRepository = OrderRepository()
    private val userRepository = UserRepository()

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> get() = _orders

    private val _revenue = MutableLiveData<Double>()
    val revenue: LiveData<Double> get() = _revenue

    private val _soldCount = MutableLiveData<Int>()
    val soldCount: LiveData<Int> get() = _soldCount

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadDashboardData(sellerId: String) {
        viewModelScope.launch {
            // Load user info
            userRepository.getUserById(sellerId).onSuccess { user ->
                _user.value = user
                println("User loaded: $user")
                // Sử dụng revenue và soldCount từ User
                _revenue.value = user.revenue
                _soldCount.value = user.soldCount
            }.onFailure { e ->
                _error.value = e.message ?: "Lỗi khi tải thông tin người bán"
            }

            // Load orders (chỉ để hiển thị danh sách, không tính revenue/soldCount)
            orderRepository.getOrdersBySeller(sellerId).onSuccess { orders ->
                println("Orders loaded: $orders")
                _orders.value = orders.sortedByDescending { it.createdAt?.toDate() }
            }.onFailure { e ->
                _error.value = e.message ?: "Lỗi khi tải đơn hàng"
                println("Error loading orders: ${e.message}")
            }
        }
    }
}