package com.example.ecommerce.viewmodel.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Category
import com.example.ecommerce.model.Product
import com.example.ecommerce.repository.common.CategoryRepository
import com.example.ecommerce.repository.common.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val productRepository = ProductRepository()
    private val categoryRepository = CategoryRepository()

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _filteredProducts = MutableLiveData<List<Product>>(emptyList())
    val filteredProducts: LiveData<List<Product>> = _filteredProducts

    private var currentFilter: String = "all"
    private var currentSort: String = "newest"
    private var searchQuery: String = ""

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val _categoriesError = MutableLiveData<String>()
    val categoriesError: LiveData<String> get() = _categoriesError

    private val _addProductSuccess = MutableLiveData<Boolean>()
    val addProductSuccess: LiveData<Boolean> get() = _addProductSuccess

    private val _addProductError = MutableLiveData<String>()
    val addProductError: LiveData<String> get() = _addProductError

    private val _updateProductSuccess = MutableLiveData<Boolean>()
    val updateProductSuccess: LiveData<Boolean> get() = _updateProductSuccess

    private val _updateProductError = MutableLiveData<String>()
    val updateProductError: LiveData<String> get() = _updateProductError

    private val _deleteProductSuccess = MutableLiveData<Boolean>()
    val deleteProductSuccess: LiveData<Boolean> get() = _deleteProductSuccess

    private val _deleteProductError = MutableLiveData<String>()
    val deleteProductError: LiveData<String> get() = _deleteProductError

    init {
        fetchCategories()
        fetchProducts()
    }

    fun fetchProducts() {
        viewModelScope.launch {
            val sellerId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
                _deleteProductError.value = "Người dùng chưa đăng nhập"
                return@launch
            }
            val products = productRepository.getProductsBySeller(sellerId)
            _products.value = products
            applyFilterAndSort()
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            val result = categoryRepository.fetchCategories()
            result.onSuccess { categoryList ->
                _categories.value = categoryList
            }.onFailure { e ->
                _categoriesError.value = e.message
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            val result = productRepository.addProduct(product)
            if (result) {
                _addProductSuccess.value = true
                fetchProducts()
            } else {
                _addProductError.value = "Không thể thêm sản phẩm"
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            val result = productRepository.updateProduct(product)
            if (result) {
                _updateProductSuccess.value = true
                fetchProducts()
            } else {
                _updateProductError.value = "Không thể cập nhật sản phẩm"
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            val result = productRepository.deleteProduct(productId)
            if (result) {
                _deleteProductSuccess.value = true
                fetchProducts() // Cập nhật lại danh sách sau khi xóa
            } else {
                _deleteProductError.value = "Không thể xóa sản phẩm"
            }
        }
    }

    fun filterProducts(filter: String) {
        currentFilter = filter
        applyFilterAndSort()
    }

    fun sortProducts(sort: String) {
        currentSort = sort
        applyFilterAndSort()
    }

    fun searchProducts(query: String) {
        searchQuery = query
        applyFilterAndSort()
    }

    private fun applyFilterAndSort() {
        var filteredList = _products.value.orEmpty()

        filteredList = when (currentFilter) {
            "out_of_stock" -> filteredList.filter { it.stock == 0 }
            else -> filteredList
        }

        if (searchQuery.isNotEmpty()) {
            filteredList = filteredList.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

        filteredList = when (currentSort) {
            "price_asc" -> filteredList.sortedBy { it.price }
            "price_desc" -> filteredList.sortedByDescending { it.price }
            "sold_desc" -> filteredList.sortedByDescending { it.soldCount }
            else -> filteredList.sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
        }

        println("Danh sách sau khi lọc/sắp xếp: ${filteredList.map { it.name }}")
        _filteredProducts.value = filteredList
    }
}