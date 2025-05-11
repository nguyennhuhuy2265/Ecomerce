package com.example.ecommerce.viewmodel.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.User
import com.example.ecommerce.repository.common.UserRepository
import kotlinx.coroutines.launch

class ShopViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _sellerInfo = MutableLiveData<User>()
    val sellerInfo: LiveData<User> get() = _sellerInfo

    private val _logoutSuccess = MutableLiveData<Boolean>()
    val logoutSuccess: LiveData<Boolean> get() = _logoutSuccess

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    init {
        fetchSellerInfo()
    }

    fun fetchSellerInfo() {
        viewModelScope.launch {
            val result = userRepository.getCurrentUserInfo()
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null && user.role == "seller") {
                    _sellerInfo.value = user
                } else {
                    _error.value = "Người dùng không phải là seller"
                }
            } else {
                _error.value = "Lỗi khi lấy thông tin: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userRepository.loginWithEmail("", "") // Đăng xuất bằng email/password (cần điều chỉnh)
            _logoutSuccess.value = true
        }
    }
}