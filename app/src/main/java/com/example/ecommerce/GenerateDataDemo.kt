package com.example.ecommerce

import com.example.ecommerce.model.common.Address
import com.example.ecommerce.model.common.Banner
import com.example.ecommerce.model.common.Category
import com.example.ecommerce.model.common.OptionGroup
import com.example.ecommerce.model.common.OptionValue
import com.example.ecommerce.model.common.Product
import com.example.ecommerce.model.common.User
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class GenerateDataDemo {
    private val db = FirebaseFirestore.getInstance()
    private val currentTimestamp = Timestamp.now()

    fun generateDemoData() {
//        // Generate Users (including sellers)
//        generateUsers()
//
//        // Generate Categories
//        generateCategories()
//
//        // Generate Products
//        generateProducts()

        // Generate Banners
        generateBanners()
    }


    private fun generateBanners() {
        val banners = listOf(
            Banner(
                id = "banner3",
                imageUrl = "https://res.cloudinary.com/prod/image/upload/e_extract:prompt_(left%20frame;middle%20left%20frame;middle%20right%20frame;right%20frame)/me/frames-wall.jpg",
                createdAt = currentTimestamp
            ),
            Banner(
                id = "banner4",
                imageUrl = "https://res.cloudinary.com/prod/image/upload/e_extract:prompt_(tower;person;staircase)/me/landmark.jpg",
                createdAt = currentTimestamp
            ),
            Banner(
                id = "banner5",
                imageUrl = "https://res.cloudinary.com/prod/image/upload/e_extract:prompt_mid%20container;invert_true/me/food-bowls.jpg",
                createdAt = currentTimestamp
            ),
            Banner(
                id = "banner6",
                imageUrl = "https://res.cloudinary.com/prod/image/upload/e_extract:prompt_cat;multiple_true/me/cats-stairs.jpg",
                createdAt = currentTimestamp
            )

        )

        banners.forEach { banner ->
            db.collection("banners")
                .document(banner.id)
                .set(banner)
                .addOnSuccessListener { println("Banner ${banner.id} added successfully") }
                .addOnFailureListener { e -> println("Error adding banner ${banner.id}: $e") }
        }
    }
}