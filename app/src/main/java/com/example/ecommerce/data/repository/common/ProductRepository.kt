package com.example.ecommerce.repository

import android.util.Log
import com.example.ecommerce.model.common.Address
import com.example.ecommerce.model.common.Product
import com.example.ecommerce.model.common.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProductRepository {
    private val db = FirebaseFirestore.getInstance()
    private var lastVisible: DocumentSnapshot? = null
    private val pageSize = 10
    private val TAG = "ProductRepository"

    fun getProductsByIds(productIds: List<String>, callback: (List<Product>) -> Unit) {
        if (productIds.isEmpty()) {
            Log.d(TAG, "getProductsByIds: Product IDs is empty")
            callback(emptyList())
            return
        }
        Log.d(TAG, "getProductsByIds: Fetching products with IDs: $productIds")
        db.collection("products")
            .whereIn("id", productIds)
            .get()
            .addOnSuccessListener { result ->
                val productList = result.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Product::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, "getProductsByIds: Error mapping product ${doc.id}: $e")
                        null
                    }
                }
                Log.d(TAG, "getProductsByIds: Fetched ${productList.size} products: $productList")
                fetchSellerLocations(productList) { updatedProducts ->
                    callback(updatedProducts)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "getProductsByIds: Error fetching products", exception)
                callback(emptyList())
            }
    }

    fun getProducts(categoryId: String? = null, isInitialLoad: Boolean, callback: (List<Product>, Boolean) -> Unit) {
        Log.d(TAG, "getProducts: Starting with categoryId = $categoryId, isInitialLoad = $isInitialLoad")
        var query = db.collection("products")
            .orderBy("name")
            .limit(pageSize.toLong())

        if (categoryId != null) {
            Log.d(TAG, "getProducts: Filtering by categoryId = $categoryId")
            query = query.whereEqualTo("categoryId", categoryId)
        }

        if (isInitialLoad) {
            lastVisible = null
        }

        val finalQuery = if (lastVisible != null) {
            query.startAfter(lastVisible)
        } else {
            query
        }

        Log.d(TAG, "getProducts: Executing query: $finalQuery")
        finalQuery.get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "getProducts: Raw result size: ${result.size()}")
                val productList = result.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(Product::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, "getProducts: Error mapping product ${doc.id}: $e")
                        null
                    }
                }
                if (result.isEmpty) {
                    Log.d(TAG, "getProducts: No products found for query: categoryId = $categoryId")
                    callback(emptyList(), false)
                    return@addOnSuccessListener
                }
                Log.d(TAG, "getProducts: Fetched ${productList.size} products: $productList")
                lastVisible = result.documents.lastOrNull()
                fetchSellerLocations(productList) { updatedProducts ->
                    callback(updatedProducts, true)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "getProducts: Error fetching products", exception)
                callback(emptyList(), false)
            }
    }

    private fun fetchSellerLocations(products: List<Product>, callback: (List<Product>) -> Unit) {
        val sellerIds = products.mapNotNull { it.sellerId }.distinct()
        if (sellerIds.isEmpty()) {
            Log.d(TAG, "fetchSellerLocations: No seller IDs found")
            callback(products)
            return
        }

        Log.d(TAG, "fetchSellerLocations: Fetching users with IDs: $sellerIds")
        db.collection("users")
            .whereIn("id", sellerIds)
            .get()
            .addOnSuccessListener { snapshot ->
                val sellerMap = snapshot.documents
                    .filter { doc -> doc.getString("role") == "seller" }
                    .associate { doc ->
                        val addressField = doc.get("address")
                        val addressString = when (addressField) {
                            is String -> {
                                // Nếu address là chuỗi, giả định chỉ có tỉnh/thành phố
                                addressField
                            }
                            is Map<*, *> -> {
                                // Ánh xạ Map thành UserAddress và chỉ lấy city
                                val address = doc.get("address", Address::class.java)
                                address?.city ?: "Không xác định"
                            }
                            else -> "Không xác định"
                        }
                        doc.id to addressString
                    }
                Log.d(TAG, "fetchSellerLocations: Seller map: $sellerMap")
                val updatedProducts = products.map { product ->
                    product.copy(shopLocation = sellerMap[product.sellerId] ?: product.shopLocation)
                }
                Log.d(TAG, "fetchSellerLocations: Updated products: $updatedProducts")
                callback(updatedProducts)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "fetchSellerLocations: Error fetching users", exception)
                callback(products)
            }
    }
}