package com.example.ecommerce.config

import android.content.Context
import com.cloudinary.android.MediaManager
import com.example.ecommerce.R

object CloudinaryConfig {
    private var isInitialized = false

    fun init(context: Context) {
        if (!isInitialized) {
            // Sử dụng Application Context để tránh rò rỉ bộ nhớ
            val appContext = context.applicationContext
            val config = mapOf(
                "cloud_name" to appContext.getString(R.string.cloudinary_cloud_name),
                "api_key" to appContext.getString(R.string.cloudinary_api_key),
                "api_secret" to appContext.getString(R.string.cloudinary_api_secret)
            )
            MediaManager.init(appContext, config)
            isInitialized = true
        }
    }

    fun getMediaManager(): MediaManager {
        check(isInitialized) { "CloudinaryConfig must be initialized before use. Call init() first." }
        return MediaManager.get()
    }
}