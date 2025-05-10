package com.example.ecommerce.viewmodel.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Product
import com.example.ecommerce.repository.ProductRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository()

    private val _products = MutableLiveData<List<Product>>(emptyList())
    val products: LiveData<List<Product>> = _products

    private val _filteredProducts = MutableLiveData<List<Product>>(emptyList())
    val filteredProducts: LiveData<List<Product>> = _filteredProducts

    private var currentFilter: String = "all"
    private var currentSort: String = "newest"
    private var searchQuery: String = ""

    fun fetchProducts() {
        viewModelScope.launch {
            val result = repository.getProducts(isInitialLoad = true)
            result.onSuccess { (products, _) ->
                _products.value = products
                applyFilterAndSort()
            }.onFailure {
                _products.value = emptyList()
                _filteredProducts.value = emptyList()
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

        // Lọc theo trạng thái
        filteredList = when (currentFilter) {
            "active" -> filteredList.filter { it.stock > 0 }
            "out_of_stock" -> filteredList.filter { it.stock == 0 }
            else -> filteredList
        }

        // Lọc theo tìm kiếm
        if (searchQuery.isNotEmpty()) {
            filteredList = filteredList.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }

        // Sắp xếp
        filteredList = when (currentSort) {
            "price_asc" -> filteredList.sortedBy { it.price }
            "price_desc" -> filteredList.sortedByDescending { it.price }
            "sold_desc" -> filteredList.sortedByDescending { it.soldCount }
            else -> filteredList.sortedByDescending { it.createdAt?.toDate()?.time ?: 0L } // "newest"
        }

        _filteredProducts.value = filteredList
    }
}