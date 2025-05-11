package com.example.ecommerce.viewmodel.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Banner
import com.example.ecommerce.model.Product
import com.example.ecommerce.repository.common.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val repository = ProductRepository()

    private val _banners = MutableLiveData<List<Banner>>()
    val banners: LiveData<List<Banner>> = _banners

    private val _featuredProducts = MutableLiveData<List<Product>>(emptyList())
    val featuredProducts: LiveData<List<Product>> = _featuredProducts

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
        viewModelScope.launch {
            val products = repository.getProducts()
            _featuredProducts.value = products
        }
    }

    fun refreshFeaturedProducts() {
        _featuredProducts.value = emptyList()
        fetchFeaturedProducts()
    }
}