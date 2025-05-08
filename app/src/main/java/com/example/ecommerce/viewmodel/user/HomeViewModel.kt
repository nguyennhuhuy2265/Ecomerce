package com.example.ecommerce.ui.viewmodel

import android.util.Log
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
    private val _hasMoreData = MutableLiveData<Boolean>(true)
    val hasMoreData: LiveData<Boolean> = _hasMoreData
    private val TAG = "HomeViewModel"

    fun fetchBanners() {
        db.collection("banners")
            .get()
            .addOnSuccessListener { result ->
                val bannerList = result.toObjects(Banner::class.java)
                Log.d(TAG, "fetchBanners: Fetched ${bannerList.size} banners")
                _banners.value = bannerList
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "fetchBanners: Error fetching banners: $e")
                _banners.value = emptyList()
            }
    }

    fun fetchFeaturedProducts() {
        viewModelScope.launch {
            val result = repository.getProducts(isInitialLoad = true)
            result.onSuccess { (products, hasMore) ->
                Log.d(TAG, "fetchFeaturedProducts: Fetched ${products.size} products, hasMore=$hasMore")
                _featuredProducts.value = products
                _hasMoreData.value = hasMore
            }.onFailure { e ->
                Log.e(TAG, "fetchFeaturedProducts: Error fetching products: $e")
                _featuredProducts.value = emptyList()
                _hasMoreData.value = false
            }
        }
    }

    fun loadMoreFeaturedProducts() {
        if (_hasMoreData.value == true) {
            Log.d(TAG, "loadMoreFeaturedProducts: Attempting to load more")
            viewModelScope.launch {
                val result = repository.getProducts(isInitialLoad = false)
                result.onSuccess { (products, hasMore) ->
                    val currentList = _featuredProducts.value.orEmpty()
                    val newList = currentList + products
                    Log.d(TAG, "loadMoreFeaturedProducts: Added ${products.size} products, total=${newList.size}, hasMore=$hasMore")
                    _featuredProducts.value = newList
                    _hasMoreData.value = hasMore
                }.onFailure { e ->
                    Log.e(TAG, "loadMoreFeaturedProducts: Error loading more products: $e")
                    _hasMoreData.value = false
                }
            }
        } else {
            Log.d(TAG, "loadMoreFeaturedProducts: No more data to load, hasMoreData=${_hasMoreData.value}")
        }
    }
}