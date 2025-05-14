package com.example.ecommerce.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.User
import com.example.ecommerce.repository.OrderRepository
import com.example.ecommerce.repository.common.UserRepository
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val orderRepository = OrderRepository()

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> get() = _user

    private val _orders = MutableLiveData<List<Order>>()
    val orders: LiveData<List<Order>> get() = _orders

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        loadUserInfo()
        loadOrders()
    }

     fun loadUserInfo() {
        viewModelScope.launch {
            userRepository.getCurrentUserInfo().onSuccess { user ->
                _user.value = user
            }.onFailure { error ->
                _error.value = error.message
            }
        }
    }

     fun loadOrders() {
        viewModelScope.launch {
            val userId = userRepository.getCurrentUserId() ?: return@launch
            orderRepository.getOrdersByUserId(userId).onSuccess { orders ->
                _orders.value = orders
            }.onFailure { error ->
                _error.value = error.message
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    // Thêm phương thức updateUser
    fun updateUser(user: User) {
        viewModelScope.launch {
            userRepository.updateUser(user).onSuccess {
                _user.value = user // Cập nhật LiveData sau khi lưu thành công
            }.onFailure { error ->
                _error.value = error.message
            }
        }
    }
}