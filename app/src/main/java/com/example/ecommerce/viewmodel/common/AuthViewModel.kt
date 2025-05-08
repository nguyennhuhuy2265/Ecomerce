package com.example.ecommerce.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.repository.common.CategoryRepository
import com.example.ecommerce.model.Category
import com.example.ecommerce.model.User
import com.example.ecommerce.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val categoryRepository = CategoryRepository()

    private val _loginSuccess = MutableLiveData<User?>()
    val loginSuccess: LiveData<User?> get() = _loginSuccess

    private val _loginError = MutableLiveData<String>()
    val loginError: LiveData<String> get() = _loginError

    private val _registerSuccess = MutableLiveData<User?>()
    val registerSuccess: LiveData<User?> get() = _registerSuccess

    private val _registerError = MutableLiveData<String>()
    val registerError: LiveData<String> get() = _registerError

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val _categoriesError = MutableLiveData<String>()
    val categoriesError: LiveData<String> get() = _categoriesError

    init {
        fetchCategories()
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

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            val result = userRepository.loginWithEmail(email, password)
            result.onSuccess { user ->
                _loginSuccess.value = user
            }.onFailure { e ->
                _loginError.value = e.message
            }
        }
    }

    fun registerWithEmail(email: String, password: String, role: String, shopName: String?, category: String?) {
        viewModelScope.launch {
            val result = userRepository.registerUser(email, password, role, shopName, category)
            result.onSuccess { user ->
                _registerSuccess.value = user
            }.onFailure { e ->
                _registerError.value = e.message
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            val result = userRepository.signInWithGoogle(idToken)
            result.onSuccess { user ->
                _loginSuccess.value = user
            }.onFailure { e ->
                _loginError.value = e.message
            }
        }
    }
}
