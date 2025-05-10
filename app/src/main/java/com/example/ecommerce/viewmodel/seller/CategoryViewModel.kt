package com.example.ecommerce.seller.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ecommerce.model.Category

class CategoryViewModel : ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    fun setCategories(categories: List<Category>) {
        _categories.value = categories
    }
}