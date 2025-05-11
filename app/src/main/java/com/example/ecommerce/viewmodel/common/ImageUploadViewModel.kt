package com.example.ecommerce.viewmodel.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.repository.common.ImageUploadRepository
import com.example.ecommerce.repository.common.UploadStatus
import kotlinx.coroutines.launch

class ImageUploadViewModel(
    private val repository: ImageUploadRepository
) : ViewModel() {

    val uploadStatus: LiveData<UploadStatus> = repository.uploadStatus

    fun uploadImage(
        filePath: String,
        collection: String,
        documentId: String,
        fieldName: String = "imageUrl"
    ) {
        viewModelScope.launch {
            repository.uploadImage(filePath, collection, documentId, fieldName)
        }
    }

    fun uploadImages(
        filePaths: List<String>,
        collection: String,
        documentId: String,
        fieldName: String = "imageUrls"
    ) {
        viewModelScope.launch {
            repository.uploadImages(filePaths, collection, documentId, fieldName)
        }
    }

    class Factory(private val repository: ImageUploadRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ImageUploadViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ImageUploadViewModel(repository) as T
            }
            throw IllegalArgumentException("Không thể tạo ViewModel: ${modelClass.name}")
        }
    }
}