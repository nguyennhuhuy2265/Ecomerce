package com.example.ecommerce.repository.common

import android.util.Log
import com.example.ecommerce.model.User
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "UserRepository"

    suspend fun registerUser(email: String, password: String, role: String, shopName: String?, shopCategory: String?): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID not found")
            val user = User(
                id = userId,
                email = email,
                role = role,
                shopName = if (role == "seller") shopName else null,
                shopCategory = if (role == "seller") shopCategory else null,
                createdAt = Timestamp.now()
            )
            db.collection("users")
                .document(userId)
                .set(user)
                .await()
            Log.d(TAG, "User registered successfully: $userId")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Registration failed: $e")
            Result.failure(e)
        }
    }

    suspend fun loginWithEmail(email: String, password: String): Result<User?> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID not found")
            val snapshot = db.collection("users")
                .document(userId)
                .get()
                .await()
            val user = snapshot.toObject(User::class.java)?.apply { id = userId }
            Log.d(TAG, "Login successful: $userId")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Login failed: $e")
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val userId = result.user?.uid ?: throw Exception("User ID not found")
            val user = User(
                id = userId,
                email = result.user?.email ?: "",
                name = result.user?.displayName ?: "",
                avatarUrl = result.user?.photoUrl?.toString(),
                role = "user",
                createdAt = Timestamp.now()
            )
            db.collection("users")
                .document(userId)
                .set(user)
                .await()
            Log.d(TAG, "Google sign-in successful: $userId")
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Google sign-in failed: $e")
            Result.failure(e)
        }
    }

    suspend fun updateAvatar(userId: String, avatarUrl: String): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .update("avatarUrl", avatarUrl)
                .await()
            Log.d(TAG, "Avatar updated successfully for user: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update avatar: $e")
            Result.failure(e)
        }
    }

    suspend fun updateBanner(userId: String, bannerUrl: String): Result<Unit> {
        return try {
            db.collection("users")
                .document(userId)
                .update("bannerUrl", bannerUrl)
                .await()
            Log.d(TAG, "Banner updated successfully for user: $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update banner: $e")
            Result.failure(e)
        }
    }
}