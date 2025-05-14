package com.example.ecommerce.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Product
import com.example.ecommerce.repository.common.ProductRepository
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {
    private val productRepository = ProductRepository()

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun searchProducts(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val allProducts = productRepository.getProducts()
                val filteredProducts = if (query.isEmpty()) {
                    allProducts
                } else {
                    allProducts.filter {
                        it.name.contains(query, ignoreCase = true) ||
                                it.description.contains(query, ignoreCase = true)
                    }
                }
                _products.value = filteredProducts
            } catch (e: Exception) {
                _error.value = "Lỗi khi tìm kiếm: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}