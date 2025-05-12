package com.example.ecommerce.viewmodel.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Product
import kotlinx.coroutines.launch
import com.example.ecommerce.repository.AdminRepository
import com.example.ecommerce.model.User
class AdminViewModel : ViewModel() {
    private val repository = AdminRepository()

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _operationResult = MutableLiveData<Boolean>()
    val operationResult: LiveData<Boolean> = _operationResult

    fun fetchUsers() {
        viewModelScope.launch {
            val userList = repository.getUsers()
            _users.postValue(userList)
        }
    }

    fun updateUserRole(userId: String, role: String) {
        viewModelScope.launch {
            val success = repository.updateUserRole(userId, role)
            _operationResult.postValue(success)
            if (success) fetchUsers()
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            val success = repository.deleteUser(userId)
            if (success) fetchUsers()
            _operationResult.postValue(success)
        }
    }

    fun fetchProductsBySeller(sellerId: String) {
        viewModelScope.launch {
            val productList = repository.getProductsBySeller(sellerId)
            _products.postValue(productList)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            val success = repository.deleteProduct(productId)
            _operationResult.postValue(success)
        }
    }
}