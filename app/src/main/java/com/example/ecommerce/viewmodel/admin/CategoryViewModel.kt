package com.example.ecommerce.viewmodel.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Category
import com.example.ecommerce.repository.OperationStatus
import com.example.ecommerce.repository.common.CategoryRepository
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepository
) : ViewModel() {
    val categories: LiveData<List<Category>> = MutableLiveData()
    val operationStatus: LiveData<OperationStatus> = repository.operationStatus

    fun fetchCategories() {
        viewModelScope.launch {
            val result = repository.getCategories()
            if (result.isSuccess) {
                (categories as MutableLiveData).postValue(result.getOrNull())
            }
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            repository.addCategory(category)
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            repository.deleteCategory(categoryId)
        }
    }

    class Factory(private val repository: CategoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CategoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CategoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Không thể tạo ViewModel: ${modelClass.name}")
        }
    }
}