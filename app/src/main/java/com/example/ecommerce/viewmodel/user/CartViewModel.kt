package com.example.ecommerce.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Cart
import com.example.ecommerce.repository.CartRepository
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val cartRepository = CartRepository()

    private val _carts = MutableLiveData<List<Cart>>()
    val carts: LiveData<List<Cart>> get() = _carts

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadCart(userId: String) {
        viewModelScope.launch {
            cartRepository.getCartsByUserId(userId).onSuccess { carts ->
                _carts.value = carts
            }.onFailure { e ->
                _error.value = e.message ?: "Lỗi khi tải giỏ hàng"
            }
        }
    }

    fun removeItemFromCart(cartId: String, userId: String) {
        viewModelScope.launch {
            cartRepository.removeItemFromCart(cartId).onSuccess {
                loadCart(userId)
            }.onFailure { e ->
                _error.value = e.message ?: "Lỗi khi xóa sản phẩm"
            }
        }
    }

    fun updateQuantity(cartId: String, newQuantity: Int, userId: String) {
        viewModelScope.launch {
            val cart = _carts.value?.find { it.id == cartId }
            cart?.let {
                val updatedCart = it.copy(quantity = newQuantity)
                cartRepository.updateItemInCart(cartId, updatedCart).onSuccess {
                    loadCart(userId)
                }.onFailure { e ->
                    _error.value = e.message ?: "Lỗi khi cập nhật số lượng"
                }
            } ?: run {
                _error.value = "Không tìm thấy sản phẩm trong giỏ hàng"
            }
        }
    }
}