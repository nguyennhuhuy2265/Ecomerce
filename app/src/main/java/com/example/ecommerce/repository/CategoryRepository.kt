package com.example.ecommerce.repository.common

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ecommerce.model.Category
import com.example.ecommerce.repository.OperationStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CategoryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "CategoryRepository"
    private val _operationStatus = MutableLiveData<OperationStatus>()
    val operationStatus: LiveData<OperationStatus> = _operationStatus

    suspend fun getCategories(): Result<List<Category>> {
        return try {
            val snapshot = db.collection("categories")
                .orderBy("name")
                .get()
                .await()
            val categories = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Category::class.java)
            }
            Log.d(TAG, "Fetched ${categories.size} categories")
            Result.success(categories)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch categories: $e")
            Result.failure(e)
        }
    }
    suspend fun addCategory(category: Category): Result<Unit> {
        return try {
            val customId = category.id.ifEmpty { db.collection("categories").document().id }
            db.collection("categories").document(customId)
                .set(category.copy(id = customId))
                .await()
            _operationStatus.postValue(OperationStatus.Success("Thêm danh mục thành công"))
            Result.success(Unit)
        } catch (e: Exception) {
            _operationStatus.postValue(OperationStatus.Error("Lỗi khi thêm danh mục: ${e.message}"))
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(categoryId: String): Result<Unit> {
        return try {
            db.collection("categories").document(categoryId).delete().await()
            _operationStatus.postValue(OperationStatus.Success("Xóa danh mục thành công"))
            Result.success(Unit)
        } catch (e: Exception) {
            _operationStatus.postValue(OperationStatus.Error("Lỗi khi xóa danh mục: ${e.message}"))
            Result.failure(e)
        }
    }
}