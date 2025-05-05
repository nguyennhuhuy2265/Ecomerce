package com.example.ecommerce.ui.user.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce.model.common.Banner
import com.example.ecommerce.model.common.Product
import com.example.ecommerce.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val repository = ProductRepository()
    private val auth = FirebaseAuth.getInstance()
    private val _banners = MutableLiveData<List<Banner>>()
    private val _featuredProducts = MutableLiveData<List<Product>>(emptyList())
    private val _hasMoreData = MutableLiveData<Boolean>(true)
    val banners: LiveData<List<Banner>> = _banners
    val featuredProducts: LiveData<List<Product>> = _featuredProducts
    val hasMoreData: LiveData<Boolean> = _hasMoreData

    fun fetchBanners() {
        db.collection("banners")
            .get()
            .addOnSuccessListener { result ->
                val bannerList = result.toObjects(Banner::class.java)
                _banners.value = bannerList
            }
            .addOnFailureListener { exception ->
                _banners.value = emptyList()
            }
    }


    fun fetchFeaturedProducts(categoryId: String? = null) {
        repository.getProducts(categoryId, isInitialLoad = true) { products, hasMore ->
            val featured = products.filter { product ->
                (categoryId == null || product.categoryId == categoryId) && product.avgRating >= 3.0
            }
            _featuredProducts.value = featured
            _hasMoreData.value = hasMore
        }
    }

    fun loadMoreFeaturedProducts() {
        if (_hasMoreData.value == true) {
            repository.getProducts(categoryId = null, isInitialLoad = false) { products, hasMore ->
                val currentFeatured = _featuredProducts.value.orEmpty()
                val newFeatured = (currentFeatured + products).filter { it.avgRating >= 3.0 }
                _featuredProducts.value = newFeatured
                _hasMoreData.value = hasMore
            }
        }
    }
}