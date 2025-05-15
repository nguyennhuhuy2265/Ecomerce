package com.example.ecommerce.viewmodel.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Product
import com.example.ecommerce.model.Review
import com.example.ecommerce.repository.ReviewRepository
import com.example.ecommerce.repository.common.ProductRepository
import kotlinx.coroutines.launch

class ManageReviewViewModel(
    private val productRepo: ProductRepository = ProductRepository(),
    private val reviewRepo: ReviewRepository = ReviewRepository()
) : ViewModel() {

    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> get() = _product

    private val _reviews = MutableLiveData<List<Review>>()
    val reviews: LiveData<List<Review>> get() = _reviews

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadReviews(productId: String) {
        viewModelScope.launch {
            // Lấy thông tin sản phẩm
            productRepo.getProductById(productId).onSuccess { prod ->
                _product.value = prod
            }.onFailure { _error.value = "Lỗi khi lấy sản phẩm: ${it.message}" }

            // Lấy danh sách đánh giá
            reviewRepo.getReviewsByProductId(productId).onSuccess { reviews ->
                _reviews.value = reviews
            }.onFailure { _error.value = "Lỗi khi lấy đánh giá: ${it.message}" }
        }
    }
}