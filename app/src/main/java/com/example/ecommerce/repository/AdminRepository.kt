package com.example.ecommerce.repository

import com.example.ecommerce.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.example.ecommerce.model.User
import com.google.firebase.Timestamp
class AdminRepository {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val productsCollection = db.collection("products")

    // Lấy danh sách người dùng, ngoại trừ admin@gmail.com
    suspend fun getUsers(): List<User> = withContext(Dispatchers.IO) {
        try {
            val snapshot = usersCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                val user = doc.toObject(User::class.java)?.copy(id = doc.id)
                if (user?.email != "admin@gmail.com") user else null
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Cập nhật vai trò người dùng
    suspend fun updateUserRole(userId: String, role: String): Boolean = withContext(Dispatchers.IO) {
        try {
            usersCollection.document(userId)
                .update(
                    mapOf(
                        "role" to role,
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Xóa người dùng
    suspend fun deleteUser(userId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            usersCollection.document(userId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Lấy danh sách sản phẩm của một seller
    suspend fun getProductsBySeller(sellerId: String): List<Product> = withContext(Dispatchers.IO) {
        try {
            val snapshot = productsCollection.whereEqualTo("sellerId", sellerId).get().await()
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(Product::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Xóa sản phẩm
    suspend fun deleteProduct(productId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            productsCollection.document(productId).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}