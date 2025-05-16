package com.example.ecommerce.viewmodel.admin

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.repository.BannerRepository
import com.example.ecommerce.repository.OperationStatus
import kotlinx.coroutines.launch

class BannerViewModel(
    private val repository: BannerRepository
) : ViewModel() {
    val operationStatus: LiveData<OperationStatus> = repository.operationStatus

    fun addBanner(imagePath: String) {
        viewModelScope.launch {
            repository.addBanner(imagePath)
        }
    }

    fun deleteBanner(bannerId: String) {
        viewModelScope.launch {
            repository.deleteBanner(bannerId)
        }
    }

    class Factory(private val repository: BannerRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BannerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BannerViewModel(repository) as T
            }
            throw IllegalArgumentException("Không thể tạo ViewModel: ${modelClass.name}")
        }
    }
}