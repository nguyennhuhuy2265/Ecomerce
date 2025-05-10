package com.example.ecommerce.repository

import android.util.Log
import com.example.ecommerce.model.Product
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private var lastVisible: DocumentSnapshot? = null
    private val pageSize = 10
    private val TAG = "ProductRepository"

    suspend fun getProducts(isInitialLoad: Boolean): Result<Pair<List<Product>, Boolean>> {
        return try {
            if (isInitialLoad) {
                lastVisible = null
            }

            var query = db.collection("products")
                .orderBy("name")
                .limit(pageSize.toLong())

            if (lastVisible != null) {
                Log.d(TAG, "getProducts: Loading more with lastVisible=$lastVisible")
                query = query.startAfter(lastVisible)
            } else {
                Log.d(TAG, "getProducts: Initial load")
            }

            val snapshot = query.get().await()
            val products = snapshot.documents.mapNotNull { doc ->
                try {
                    val product = doc.toObject(Product::class.java)
                    if (product == null) {
                        Log.w(TAG, "Failed to map document ${doc.id}")
                    }
                    product
                } catch (e: Exception) {
                    Log.e(TAG, "Error mapping document ${doc.id}: $e")
                    null
                }
            }
            lastVisible = snapshot.documents.lastOrNull()
            val hasMore = products.isNotEmpty() && lastVisible != null
            Log.d(TAG, "getProducts: Fetched ${products.size} products, hasMore=$hasMore, lastVisible=$lastVisible")
            Result.success(Pair(products, hasMore))
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching products: $e")
            Result.failure(e)
        }
    }

    suspend fun addProduct(product: Product): Result<Unit> {
        return try {
            db.collection("products")
                .add(product)
                .await()
            Log.d(TAG, "Product added successfully: ${product.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to add product: $e")
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            db.collection("products")
                .document(product.id)
                .set(product)
                .await()
            Log.d(TAG, "Product updated successfully: ${product.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update product: $e")
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            db.collection("products")
                .document(productId)
                .delete()
                .await()
            Log.d(TAG, "Product deleted successfully: $productId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete product: $e")
            Result.failure(e)
        }
    }

    suspend fun updateProductImage(productId: String, imagePublicIds: List<String>, defaultImagePublicId: String): Result<Unit> {
        return try {
            db.collection("products")
                .document(productId)
                .update(
                    mapOf(
                        "image_public_ids" to imagePublicIds,
                        "default_image_public_id" to defaultImagePublicId
                    )
                )
                .await()
            Log.d(TAG, "Product image updated successfully: $productId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update product image: $e")
            Result.failure(e)
        }
    }
}