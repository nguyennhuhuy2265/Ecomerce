package com.example.ecommerce

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerce.ui.common.LoginActivity
import com.example.ecommerce.ui.seller.SellerMainActivity
import com.example.ecommerce.ui.user.UserMainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

        auth = FirebaseAuth.getInstance()

        Handler(Looper.getMainLooper()).postDelayed({
//            checkLoginStatus()
      startActivity(Intent(this, LoginActivity::class.java))
        }, 1000) // Delay 1s để show splash nhẹ
    }

    private fun checkLoginStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Lấy role từ Firestore
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val userId = currentUser.uid
                    val userDoc = db.collection("users")
                        .document(userId)
                        .get()
                        .await()
                    val role = userDoc.getString("role") ?: "user" // Mặc định là "user" nếu không tìm thấy role
                    Log.d(TAG, "User role: $role")

                    // Điều hướng dựa trên role
                    if (role == "seller") {
                        startActivity(Intent(this@MainActivity, SellerMainActivity::class.java))
                    } else if (role == "user") {
                        startActivity(Intent(this@MainActivity, UserMainActivity::class.java))
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to fetch user role: $e")
                    // Nếu có lỗi (ví dụ: không có mạng), điều hướng mặc định tới LoginActivity
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                } finally {
                    finish()
                }
            }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
}