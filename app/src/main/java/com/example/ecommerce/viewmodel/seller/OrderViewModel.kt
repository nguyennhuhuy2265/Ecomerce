package com.example.ecommerce.viewmodel.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.repository.OrderRepository
import kotlinx.coroutines.launch

class OrderViewModel : ViewModel() {
    private val orderRepository = OrderRepository()

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> get() = _orders

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private var allOrders: List<Order> = emptyList()

    fun loadOrdersBySeller(sellerId: String) {
        viewModelScope.launch {
            orderRepository.getOrdersBySeller(sellerId).onSuccess { orders ->
                allOrders = orders
                _orders.value = allOrders
            }.onFailure { e ->
                _error.value = e.message ?: "Lỗi khi tải đơn hàng"
            }
        }
    }

    fun filterOrdersByStatus(status: OrderStatus?) {
        _orders.value = if (status == null) allOrders else allOrders.filter { it.status == status }
    }
}