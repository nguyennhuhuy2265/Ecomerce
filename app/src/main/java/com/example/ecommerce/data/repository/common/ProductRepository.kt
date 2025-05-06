package com.example.ecommerce.data.repository

import android.util.Log
import com.example.ecommerce.model.common.Product
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private var lastVisible: DocumentSnapshot? = null
    private val pageSize = 10
    private val TAG = "ProductRepository"

    fun getProducts(isInitialLoad: Boolean, callback: (List<Product>, Boolean, DocumentSnapshot?) -> Unit) {
        var query = db.collection("products")
            .orderBy("name")
            .limit(pageSize.toLong())

        if (isInitialLoad) {
            lastVisible = null
        }

        val finalQuery = if (lastVisible != null) {
            Log.d(TAG, "getProducts: Loading more with lastVisible=$lastVisible")
            query.startAfter(lastVisible)
        } else {
            Log.d(TAG, "getProducts: Initial load")
            query
        }

        finalQuery.get()
            .addOnSuccessListener { result ->
                val products = result.documents.mapNotNull { doc ->
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
                lastVisible = result.documents.lastOrNull()
                val hasMore = products.isNotEmpty() && lastVisible != null
                Log.d(TAG, "getProducts: Fetched ${products.size} products, hasMore=$hasMore, lastVisible=$lastVisible")
                callback(products, hasMore, lastVisible)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error fetching products: $e")
                callback(emptyList(), false, null)
            }
    }
}