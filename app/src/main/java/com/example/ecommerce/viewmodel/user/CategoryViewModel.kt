package com.example.ecommerce.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Category
import com.example.ecommerce.model.Product
import com.example.ecommerce.repository.common.CategoryRepository
import com.example.ecommerce.repository.common.ProductRepository
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {
    private val categoryRepository = CategoryRepository()
    private val productRepository = ProductRepository()

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _selectedCategoryId = MutableLiveData<String>()
    val selectedCategoryId: LiveData<String> get() = _selectedCategoryId

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        loadCategories()
    }

    private fun loadCategories() {
        _isLoading.value = true
        viewModelScope.launch {
            categoryRepository.getCategories().onSuccess { categories ->
                _categories.value = categories
                // Chọn danh mục "fashion" làm mặc định nếu có, nếu không chọn danh mục đầu tiên
                val defaultCategory = categories.firstOrNull { it.id == "computers_phones" } ?: categories.firstOrNull()
                defaultCategory?.let {
                    _selectedCategoryId.value = it.id
                    loadProducts(it.id)
                }
            }.onFailure { _error.value = it.message }
            _isLoading.value = false
        }
    }

    fun selectCategory(categoryId: String) {
        if (_selectedCategoryId.value != categoryId) {
            _selectedCategoryId.value = categoryId
            loadProducts(categoryId)
        }
    }

    private fun loadProducts(categoryId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val allProducts = productRepository.getProducts()
            _products.value = allProducts.filter { it.categoryId == categoryId }
            _isLoading.value = false
        }
    }
}