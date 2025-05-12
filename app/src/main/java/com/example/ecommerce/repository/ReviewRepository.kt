package com.example.ecommerce.repository

import com.example.ecommerce.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReviewRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getReviewsByProductId(productId: String): Result<List<Review>> {
        return try {
            val snapshot = db.collection("reviews")
                .whereEqualTo("productId", productId)
                .limit(5)
                .get()
                .await()
            val reviews = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Review::class.java)?.copy(id = doc.id)
            }
            Result.success(reviews)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}