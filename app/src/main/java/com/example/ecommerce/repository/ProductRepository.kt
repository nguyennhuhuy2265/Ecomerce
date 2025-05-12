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
            println("Đang truy vấn sản phẩm cho sellerId: $sellerId")
            val snapshot = productsCollection
                .whereEqualTo("sellerId", sellerId)
                .get()
                .await()
            val products = snapshot.toObjects(Product::class.java)
            println("Truy vấn thành công, số lượng sản phẩm: ${products.size}")
            products
        } catch (e: Exception) {
            println("Lỗi khi lấy sản phẩm: ${e.message}")
            emptyList()
        }
    }

    suspend fun addProduct(product: Product): Boolean {
        return try {
            product.id?.let { id ->
                println("Đang thêm sản phẩm với id: $id")
                productsCollection.document(id).set(product).await()
                println("Thêm sản phẩm thành công")
                true
            } ?: run {
                println("ID sản phẩm không hợp lệ")
                false
            }
        } catch (e: Exception) {
            println("Lỗi khi thêm sản phẩm: ${e.message}")
            false
        }
    }

    suspend fun updateProduct(product: Product): Boolean {
        return try {
            product.id?.let { id ->
                println("Đang cập nhật sản phẩm với id: $id")
                productsCollection.document(id).set(product).await()
                println("Cập nhật sản phẩm thành công")
                true
            } ?: run {
                println("ID sản phẩm không hợp lệ")
                false
            }
        } catch (e: Exception) {
            println("Lỗi khi cập nhật sản phẩm: ${e.message}")
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

    suspend fun getProductById(productId: String): Result<Product> {
        return try {
            val snapshot = firestore.collection("products").document(productId).get().await()
            val product = snapshot.toObject(Product::class.java)?.copy(id = snapshot.id)
            if (product != null) {
                Result.success(product)
            } else {
                Result.failure(Exception("Sản phẩm không tồn tại"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}