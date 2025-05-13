package com.example.ecommerce.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Address
import com.example.ecommerce.model.Cart
import com.example.ecommerce.model.Notification
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.model.PaymentStatus
import com.example.ecommerce.model.Product
import com.example.ecommerce.model.User
import com.example.ecommerce.repository.OrderRepository
import com.example.ecommerce.repository.NotificationRepository
import com.example.ecommerce.repository.common.UserRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class CheckoutViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val orderRepository = OrderRepository()
    private val notificationRepository = NotificationRepository()

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> get() = _user

    private val _checkoutItems = MutableLiveData<List<Cart>>()
    val checkoutItems: LiveData<List<Cart>> get() = _checkoutItems

    private val _orderActionResult = MutableLiveData<String>()
    val orderActionResult: LiveData<String> get() = _orderActionResult

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadUser(userId: String) {
        viewModelScope.launch {
            userRepository.getUserById(userId).onSuccess { user ->
                _user.value = user
            }.onFailure { e ->
                _error.value = e.message ?: "Lỗi khi tải thông tin người dùng"
            }
        }
    }

    fun loadCheckoutItemsFromCart(cart: Cart) {
        _checkoutItems.value = listOf(cart)
    }

    fun loadCheckoutItemsFromProduct(product: Product, quantity: Int, selectedOptions: List<String>) {
        val cart = Cart(
            id = "",
            userId = "",
            sellerId = product.sellerId,
            productId = product.id,
            productName = product.name,
            productImage = product.imageUrls.firstOrNull() ?: "",
            unitPrice = product.price.toDouble(),
            quantity = quantity,
            selectedOptions = selectedOptions
        )
        _checkoutItems.value = listOf(cart)
    }

    fun placeOrder(userId: String, paymentStatus: PaymentStatus, address: Address?) {
        viewModelScope.launch {
            if (address == null) {
                _error.value = "Địa chỉ không hợp lệ"
                return@launch
            }

            val items = _checkoutItems.value ?: return@launch

            items.forEach { cart ->
                val order = Order(
                    id = "",
                    userId = userId,
                    sellerId = cart.sellerId,
                    productId = cart.productId,
                    productName = cart.productName,
                    productImage = cart.productImage,
                    unitPrice = cart.unitPrice,
                    quantity = cart.quantity,
                    selectedOptions = cart.selectedOptions,
                    totalAmount = cart.unitPrice * cart.quantity,
                    paymentStatus = paymentStatus,
                    status = OrderStatus.PENDING,
                    shippingAddress = address,
                    createdAt = Timestamp.now()
                )

                orderRepository.createOrder(userId, order).onSuccess { orderId ->
                    // Gửi thông báo cho người mua
                    notificationRepository.sendNotification(
                        userId = userId,
                        title = "Đặt hàng thành công",
                        body = "Đơn hàng của bạn Mã: $orderId đã được đặt. Vui lòng theo dõi trạng thái.",
                        orderId = orderId
                    )

                    // Gửi thông báo cho người bán
                    notificationRepository.sendNotification(
                        userId = cart.sellerId,
                        title = "Đơn hàng mới",
                        body = "Bạn có đơn hàng mới Mã: $orderId từ người dùng $userId.",
                        orderId = orderId
                    )

                    // Cập nhật revenue và soldCount của người bán
                    userRepository.getUserById(cart.sellerId).onSuccess { seller ->
                        val updatedSeller = seller.copy(
                            revenue = seller.revenue + (cart.unitPrice * cart.quantity),
                            soldCount = seller.soldCount + cart.quantity,
                            updatedAt = Timestamp.now()
                        )
                        userRepository.updateUser(updatedSeller)
                    }

                    _orderActionResult.value = orderId
                }.onFailure { e ->
                    _error.value = e.message ?: "Lỗi khi đặt hàng"
                }
            }
        }
    }
}