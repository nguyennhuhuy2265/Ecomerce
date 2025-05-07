package com.example.ecommerce.data.remote

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Khởi tạo Cloudinary
        MediaManager.init(this, mapOf("cloud_name" to "dpcoggnin"))
    }
}