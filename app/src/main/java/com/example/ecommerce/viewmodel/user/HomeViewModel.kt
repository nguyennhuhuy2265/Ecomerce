package com.example.ecommerce.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Banner
import com.example.ecommerce.model.Product
import com.example.ecommerce.repository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val repository = ProductRepository()

    private val _banners = MutableLiveData<List<Banner>>()
    val banners: LiveData<List<Banner>> = _banners

    private val _featuredProducts = MutableLiveData<List<Product>>(emptyList())
    val featuredProducts: LiveData<List<Product>> = _featuredProducts

    private val _hasMoreData = MutableLiveData(true)
    val hasMoreData: LiveData<Boolean> = _hasMoreData

    private var isLoading = false

    fun fetchBanners() {
        db.collection("banners")
            .get()
            .addOnSuccessListener { result ->
                _banners.value = result.toObjects(Banner::class.java)
            }
            .addOnFailureListener {
                _banners.value = emptyList()
            }
    }

    fun fetchFeaturedProducts() {
        if (isLoading) return
        isLoading = true
        viewModelScope.launch {
            repository.getProducts(isInitialLoad = true).onSuccess { (products, hasMore) ->
                _featuredProducts.value = products
                _hasMoreData.value = hasMore
            }.onFailure {
                _featuredProducts.value = emptyList()
                _hasMoreData.value = false
            }
            isLoading = false
        }
    }

    fun loadMoreFeaturedProducts() {
        if (isLoading || _hasMoreData.value != true) return
        isLoading = true
        viewModelScope.launch {
            repository.getProducts(isInitialLoad = false).onSuccess { (products, hasMore) ->
                _featuredProducts.value = _featuredProducts.value.orEmpty() + products
                _hasMoreData.value = hasMore
            }.onFailure {
                _hasMoreData.value = false
            }
            isLoading = false
        }
    }

    fun refreshFeaturedProducts() {
        _featuredProducts.value = emptyList() // Xóa danh sách cũ
        fetchFeaturedProducts() // Tải lại từ đầu
    }
}