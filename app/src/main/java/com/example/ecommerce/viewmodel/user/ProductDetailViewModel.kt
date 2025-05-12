package com.example.ecommerce.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Product
import com.example.ecommerce.model.Review
import com.example.ecommerce.model.User
import com.example.ecommerce.repository.ReviewRepository
import com.example.ecommerce.repository.common.ProductRepository
import com.example.ecommerce.repository.common.UserRepository
import kotlinx.coroutines.launch

class ProductDetailViewModel : ViewModel() {
    private val productRepository = ProductRepository()
    private val userRepository = UserRepository()
    private val reviewRepository = ReviewRepository()

    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> get() = _product

    private val _seller = MutableLiveData<User>()
    val seller: LiveData<User> get() = _seller

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> get() = _reviews

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            // Tải sản phẩm
            val productResult = productRepository.getProductById(productId)
            productResult.onSuccess { product ->
                _product.value = product
                // Tải thông tin người bán
                val sellerResult = userRepository.getUserById(product.sellerId)
                sellerResult.onSuccess { seller ->
                    _seller.value = seller
                }.onFailure { _error.value = it.message ?: "Lỗi tải người bán" }
                // Tải đánh giá
                val reviewsResult = reviewRepository.getReviewsByProductId(productId)
                reviewsResult.onSuccess { reviews ->
                    _reviews.value = reviews
                }.onFailure { _error.value = it.message ?: "Lỗi tải đánh giá" }
            }.onFailure { _error.value = it.message ?: "Lỗi tải sản phẩm" }
        }
    }
}