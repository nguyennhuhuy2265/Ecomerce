package com.example.ecommerce.repository.common

import com.example.ecommerce.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProductRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val productsCollection = firestore.collection("products")

    suspend fun getProducts(): List<Product> {
        return try {
            val snapshot = productsCollection.get().await()
            snapshot.toObjects(Product::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getProductsBySeller(sellerId: String): List<Product> {
        return try {
            val snapshot = productsCollection
                .whereEqualTo("sellerId", sellerId)
                .get()
                .await()
            snapshot.toObjects(Product::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addProduct(product: Product): Boolean {
        return try {
            product.id?.let { id ->
                productsCollection.document(id).set(product).await()
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateProduct(product: Product): Boolean {
        return try {
            product.id?.let { id ->
                productsCollection.document(id).set(product).await()
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteProduct(productId: String): Boolean {
        return try {
            productsCollection.document(productId).delete().await()
            println("Sản phẩm $productId đã được xóa thành công")
            true
        } catch (e: Exception) {
            println("Lỗi khi xóa sản phẩm $productId: ${e.message}")
            false
        }
    }
}