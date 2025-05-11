package com.example.ecommerce.config

import android.app.Application
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
        cloudinary = Cloudinary(config)
    }
}
