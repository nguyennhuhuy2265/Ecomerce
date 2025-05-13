package com.example.ecommerce.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Address
import com.example.ecommerce.model.Cart
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.Product
import com.example.ecommerce.model.Review
import com.example.ecommerce.model.User
import com.example.ecommerce.repository.CartRepository
import com.example.ecommerce.repository.OrderRepository
import com.example.ecommerce.repository.ReviewRepository
import com.example.ecommerce.repository.common.ProductRepository
import com.example.ecommerce.repository.common.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProductDetailViewModel : ViewModel() {
    private val productRepository = ProductRepository()
    private val userRepository = UserRepository()
    private val reviewRepository = ReviewRepository()
    private val cartRepository = CartRepository()
    private val orderRepository = OrderRepository()
    private val auth = FirebaseAuth.getInstance()

    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> get() = _product

    private val _seller = MutableLiveData<User>()
    val seller: LiveData<User> get() = _seller

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> get() = _reviews

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _cartActionResult = MutableLiveData<Boolean>()
    val cartActionResult: LiveData<Boolean> get() = _cartActionResult

    private val _orderActionResult = MutableLiveData<String>()
    val orderActionResult: LiveData<String> get() = _orderActionResult

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            productRepository.getProductById(productId).onSuccess { product ->
                _product.value = product
                userRepository.getUserById(product.sellerId).onSuccess { seller ->
                    _seller.value = seller
                }.onFailure { _error.value = it.message }
                reviewRepository.getReviewsByProductId(productId).onSuccess { reviews ->
                    _reviews.value = reviews
                }.onFailure { _error.value = it.message }
            }.onFailure { _error.value = it.message }
        }
    }

    fun addToCart(product: Product, selectedOptions: List<String>, quantity: Int) {
        viewModelScope.launch {
            val userId = auth.currentUser?.uid
            if (userId == null) {
                _error.value = "Vui lòng đăng nhập"
                return@launch
            }

            val cart = Cart(
                userId = userId,
                sellerId = product.sellerId,
                productId = product.id,
                productName = product.name,
                productImage = product.imageUrls.firstOrNull() ?: "",
                unitPrice = product.price.toDouble(),
                quantity = quantity,
                selectedOptions = selectedOptions
            )
            cartRepository.addItemToCart(userId, cart).onSuccess {
                _cartActionResult.value = true
            }.onFailure { e ->
                _error.value = e.message ?: "Lỗi khi thêm vào giỏ hàng"
                _cartActionResult.value = false
            }
        }
    }

    // Các phương thức khác (buyNow, createOrderFromCart) giữ nguyên hoặc thêm sau
}