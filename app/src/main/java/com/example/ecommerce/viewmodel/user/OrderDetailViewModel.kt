package com.example.ecommerce.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Order
import com.example.ecommerce.repository.OrderRepository
import kotlinx.coroutines.launch

class OrderDetailViewModel : ViewModel() {

    private val orderRepository = OrderRepository()

    private val _order = MutableLiveData<Order?>()
    val order: LiveData<Order?> get() = _order

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            orderRepository.getOrderById(orderId).onSuccess { order ->
                _order.value = order
            }.onFailure {
                _order.value = null
            }
        }
    }
}