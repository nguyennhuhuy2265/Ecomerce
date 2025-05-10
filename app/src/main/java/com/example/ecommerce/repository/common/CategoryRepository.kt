package com.example.ecommerce.repository.common

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.ecommerce.model.Category
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CategoryRepository {
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "CategoryRepository"

    suspend fun fetchCategories(): Result<List<Category>> {
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
}