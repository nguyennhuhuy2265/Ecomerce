package com.example.ecommerce.repository

import com.example.ecommerce.model.Cart
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.util.UUID

class CartRepository {
    private val db = FirebaseFirestore.getInstance()
    private val cartsCollection = db.collection("carts")

    suspend fun addItemToCart(userId: String, cart: Cart): Result<Unit> {
        return try {
            val newCart = cart.copy(
                id = "${cart.productId}_${cart.selectedOptions.joinToString("_")}_${UUID.randomUUID()}"
            )
            cartsCollection.document(newCart.id).set(newCart).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}