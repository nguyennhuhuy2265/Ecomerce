package com.example.ecommerce.viewmodel.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Address
import com.example.ecommerce.model.Category
import com.example.ecommerce.model.User
import com.example.ecommerce.repository.common.CategoryRepository
import com.example.ecommerce.repository.common.UserRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch

class EditShopViewModel : ViewModel() {
    private val userRepository = UserRepository()
    private val categoryRepository = CategoryRepository()

    private val _userInfo = MutableLiveData<User>()
    val userInfo: LiveData<User> get() = _userInfo

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> get() = _updateSuccess

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        fetchUserInfo()
        fetchCategories()
    }

    fun fetchUserInfo() {
        viewModelScope.launch {
            val result = userRepository.getCurrentUserInfo()
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null && user.role == "seller") {
                    _userInfo.value = user
                } else {
                    _error.value = "Người dùng không phải là seller"
                }
            } else {
                _error.value = "Lỗi khi lấy thông tin: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            val result = categoryRepository.getCategories()
            if (result.isSuccess) {
                _categories.value = result.getOrNull() ?: emptyList()
            } else {
                _error.value = "Lỗi khi lấy danh mục: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun updateAvatar(avatarUrl: String) {
        viewModelScope.launch {
            val userId = _userInfo.value?.id ?: return@launch
            val result = userRepository.updateAvatar(userId, avatarUrl)
            if (result.isSuccess) {
                _userInfo.value = _userInfo.value?.copy(avatarUrl = avatarUrl)
            } else {
                _error.value = "Lỗi khi cập nhật ảnh: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun saveChanges(
        name: String,
        phoneNumber: String,
        shopName: String,
        shopCategory: String,
        address: Address
    ) {
        viewModelScope.launch {
            val user = _userInfo.value ?: return@launch
            val updatedUser = user.copy(
                name = name,
                phoneNumber = phoneNumber,
                shopName = shopName,
                shopCategory = shopCategory,
                address = address,
                updatedAt = Timestamp.now()
            )
            val result = userRepository.updateUser(updatedUser)
            if (result.isSuccess) {
                _updateSuccess.value = true
                fetchUserInfo()
            } else {
                _error.value = "Lỗi khi lưu thay đổi: ${result.exceptionOrNull()?.message}"
            }
        }
    }
}