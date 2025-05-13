package com.example.ecommerce.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Cart
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.model.PaymentStatus
import com.example.ecommerce.repository.CartRepository
import com.example.ecommerce.repository.OrderRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class CartViewModel : ViewModel() {
    private val cartRepository = CartRepository()
    private val orderRepository = OrderRepository()

    private val _carts = MutableLiveData<List<Cart>>()
    val carts: LiveData<List<Cart>> get() = _carts

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _orderActionResult = MutableLiveData<String>()
    val orderActionResult: LiveData<String> get() = _orderActionResult

    fun loadCart(userId: String) {
        viewModelScope.launch {
            cartRepository.getCartsByUserId(userId).onSuccess { carts ->
                _carts.value = carts
            }.onFailure { e ->
                _error.value = e.message ?: "Lỗi khi tải giỏ hàng"
            }
        }
    }

    fun removeItemFromCart(cartId: String, userId: String) { // Đã thêm userId làm tham số
        viewModelScope.launch {
            cartRepository.removeItemFromCart(cartId).onSuccess {
                loadCart(userId) // Sử dụng userId được truyền vào
            }.onFailure { e ->
                _error.value = e.message ?: "Lỗi khi xóa sản phẩm"
            }
        }
    }

    fun updateQuantity(cartId: String, newQuantity: Int, userId: String) { // Thêm userId vào đây
        viewModelScope.launch {
            val cart = _carts.value?.find { it.id == cartId }
            cart?.let {
                val updatedCart = it.copy(quantity = newQuantity)
                cartRepository.updateItemInCart(cartId, updatedCart).onSuccess {
                    loadCart(userId) // Sử dụng userId được truyền vào
                }.onFailure { e ->
                    _error.value = e.message ?: "Lỗi khi cập nhật số lượng"
                }
            } ?: run {
                _error.value = "Không tìm thấy sản phẩm trong giỏ hàng"
            }
        }
    }

    fun createOrderFromCartItem(cart: Cart) {
        viewModelScope.launch {
            val order = Order(
                id = "", // Sẽ được gán trong OrderRepository
                userId = cart.userId,
                sellerId = cart.sellerId,
                productId = cart.productId,
                productName = cart.productName,
                productImage = cart.productImage,
                unitPrice = cart.unitPrice,
                quantity = cart.quantity,
                selectedOptions = cart.selectedOptions,
                totalAmount = cart.unitPrice * cart.quantity,
                paymentStatus = PaymentStatus.PENDING,
                status = OrderStatus.PENDING,
                createdAt = Timestamp.now()
            )

            orderRepository.createOrder(cart.userId, order).onSuccess { orderId ->
                cartRepository.removeItemFromCart(cart.id).onSuccess {
                    loadCart(cart.userId)
                    _orderActionResult.value = orderId
                }.onFailure { e ->
                    _error.value = e.message ?: "Lỗi khi xóa sản phẩm khỏi giỏ hàng"
                }
            }.onFailure { e ->
                _error.value = e.message ?: "Lỗi khi tạo đơn hàng"
            }
        }
    }
}