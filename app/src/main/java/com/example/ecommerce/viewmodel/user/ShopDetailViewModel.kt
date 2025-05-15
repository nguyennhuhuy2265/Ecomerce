package com.example.ecommerce.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Product
import com.example.ecommerce.model.User
import com.example.ecommerce.repository.common.ProductRepository
import com.example.ecommerce.repository.common.UserRepository
import kotlinx.coroutines.launch

class ShopDetailViewModel(
    private val userRepo: UserRepository = UserRepository(),
    private val productRepo: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _seller = MutableLiveData<User>()
    val seller: LiveData<User> get() = _seller

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> get() = _products

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadShopDetails(sellerId: String) {
        viewModelScope.launch {
            // Lấy thông tin shop
            userRepo.getUserById(sellerId).onSuccess { user ->
                if (user.role == "seller") {
                    _seller.value = user
                } else {
                    _error.value = "Không tìm thấy shop"
                }
            }.onFailure {
                _error.value = "Lỗi khi lấy thông tin shop: ${it.message}"
            }

            // Lấy danh sách sản phẩm của shop
            val products = productRepo.getProductsBySeller(sellerId)
            _products.value = products
        }
    }
}