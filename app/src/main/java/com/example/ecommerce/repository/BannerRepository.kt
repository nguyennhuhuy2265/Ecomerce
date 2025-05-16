package com.example.ecommerce.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ecommerce.model.Banner
import com.example.ecommerce.repository.common.ImageUploadRepository
import com.example.ecommerce.repository.common.UploadStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class BannerRepository(
    private val imageUploadRepository: ImageUploadRepository
) {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "BannerRepository"
    private val _operationStatus = MutableLiveData<OperationStatus>()
    val operationStatus: LiveData<OperationStatus> = _operationStatus

    suspend fun addBanner(imagePath: String): Result<String> {
        return try {
            val newId = db.collection("banners").document().id
            imageUploadRepository.uploadImage(imagePath, "banners", newId)
            imageUploadRepository.uploadStatus.observeForever { status ->
                when (status) {
                    is UploadStatus.Success -> {
                        val banner = Banner(id = newId, imageUrl = status.imageUrls.first())
                        db.collection("banners").document(newId)
                            .set(banner)
                            .addOnSuccessListener {
                                _operationStatus.postValue(OperationStatus.Success("Thêm banner thành công"))
                            }
                            .addOnFailureListener { e ->
                                _operationStatus.postValue(OperationStatus.Error("Lỗi khi lưu banner: ${e.message}"))
                            }
                    }
                    is UploadStatus.Error -> _operationStatus.postValue(OperationStatus.Error(status.message))
                    else -> {}
                }
            }
            Result.success(newId)
        } catch (e: Exception) {
            _operationStatus.postValue(OperationStatus.Error("Lỗi: ${e.message}"))
            Result.failure(e)
        }
    }

    suspend fun deleteBanner(bannerId: String): Result<Unit> {
        return try {
            db.collection("banners").document(bannerId).delete().await()
            _operationStatus.postValue(OperationStatus.Success("Xóa banner thành công"))
            Result.success(Unit)
        } catch (e: Exception) {
            _operationStatus.postValue(OperationStatus.Error("Lỗi khi xóa banner: ${e.message}"))
            Result.failure(e)
        }
    }
}

sealed class OperationStatus {
    data class Success(val message: String) : OperationStatus()
    data class Error(val message: String) : OperationStatus()
}