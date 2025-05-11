package com.example.ecommerce.repository.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cloudinary.Cloudinary
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ImageUploadRepository(
    private val cloudinary: Cloudinary,
    private val firestore: FirebaseFirestore
) {
    private val _uploadStatus = MutableLiveData<UploadStatus>()
    val uploadStatus: LiveData<UploadStatus> = _uploadStatus

    suspend fun uploadImage(
        filePath: String,
        collection: String,
        documentId: String,
        fieldName: String = "imageUrls"
    ) {
        try {
            _uploadStatus.postValue(UploadStatus.Loading)

            val uploadResult = withContext(Dispatchers.IO) {
                cloudinary.uploader().upload(filePath, mapOf("folder" to collection))
            }
            val imageUrl = uploadResult["secure_url"] as? String
                ?: throw Exception("Không tìm thấy secure_url trong kết quả upload")

            // Lấy danh sách imageUrls hiện tại từ Firestore
            val currentDoc = firestore.collection(collection).document(documentId).get().await()
            val currentImageUrls = currentDoc.get(fieldName) as? List<String> ?: emptyList()
            val updatedImageUrls = currentImageUrls + imageUrl

            firestore.collection(collection).document(documentId)
                .set(mapOf(fieldName to updatedImageUrls), SetOptions.merge())
                .addOnSuccessListener {
                    _uploadStatus.postValue(UploadStatus.Success(listOf(imageUrl)))
                }
                .addOnFailureListener { e ->
                    _uploadStatus.postValue(UploadStatus.Error("Lỗi khi lưu vào Firestore: ${e.message}"))
                }

        } catch (e: Exception) {
            _uploadStatus.postValue(UploadStatus.Error("Lỗi upload: ${e.message}"))
        }
    }

    suspend fun uploadImages(
        filePaths: List<String>,
        collection: String,
        documentId: String,
        fieldName: String = "imageUrls"
    ) {
        try {
            _uploadStatus.postValue(UploadStatus.Loading)

            val imageUrls = mutableListOf<String>()
            for (filePath in filePaths) {
                val uploadResult = withContext(Dispatchers.IO) {
                    cloudinary.uploader().upload(filePath, mapOf("folder" to collection))
                }
                val imageUrl = uploadResult["secure_url"] as? String
                    ?: throw Exception("Không tìm thấy secure_url trong kết quả upload")
                imageUrls.add(imageUrl)
            }

            // Lấy danh sách imageUrls hiện tại từ Firestore
            val currentDoc = firestore.collection(collection).document(documentId).get().await()
            val currentImageUrls = currentDoc.get(fieldName) as? List<String> ?: emptyList()
            val updatedImageUrls = currentImageUrls + imageUrls

            firestore.collection(collection).document(documentId)
                .set(mapOf(fieldName to updatedImageUrls), SetOptions.merge())
                .addOnSuccessListener {
                    _uploadStatus.postValue(UploadStatus.Success(imageUrls))
                }
                .addOnFailureListener { e ->
                    _uploadStatus.postValue(UploadStatus.Error("Lỗi khi lưu vào Firestore: ${e.message}"))
                }

        } catch (e: Exception) {
            _uploadStatus.postValue(UploadStatus.Error("Lỗi upload: ${e.message}"))
        }
    }
}

sealed class UploadStatus {
    object Loading : UploadStatus()
    data class Success(val imageUrls: List<String>) : UploadStatus()
    data class Error(val message: String) : UploadStatus()
}