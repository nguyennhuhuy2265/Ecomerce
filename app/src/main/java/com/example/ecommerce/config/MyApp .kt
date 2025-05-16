package com.example.ecommerce.config

import android.app.Application
import android.util.Log
import com.cloudinary.Cloudinary

class MyApp : Application() {
    lateinit var cloudinary: Cloudinary

    override fun onCreate() {
        super.onCreate()
        val config = mapOf(
            "cloud_name" to "dpcoggnin",
            "api_key"    to "659752282218452",
            "api_secret" to "5yPPfGCHBBjLww_tYPH52T9Odk8"
        )
//        cloudinary = Cloudinary(config)
        try {
            // Thử khởi tạo với mapOf
            cloudinary = Cloudinary(config)
            Log.d("MyApp", "Cloudinary initialized successfully with mapOf")
        } catch (e: Exception) {
            Log.e("MyApp", "Failed to initialize Cloudinary with mapOf: ${e.message}", e)
            // Fallback sang URL hợp lệ nếu mapOf thất bại
            val fallbackConfig = "cloudinary://659752282218452:5yPPfGCHBBjLww_tYPH52T9Odk8@dpcoggnin"
            cloudinary = Cloudinary(fallbackConfig)
            Log.d("MyApp", "Cloudinary initialized successfully with fallback URL")
        }
    }
}
