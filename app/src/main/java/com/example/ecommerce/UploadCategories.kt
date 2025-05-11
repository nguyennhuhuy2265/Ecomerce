package com.example.ecommerce

import com.example.ecommerce.model.Category
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.example.ecommerce.model.OptionGroup
import com.example.ecommerce.model.Product

object CategoryUploader {

    private val db = FirebaseFirestore.getInstance()
    private const val TAG = "CategoryUploader"

    fun uploadCategories() {
        val products = listOf(
            Product(
                id = "iphone-15",
                name = "iPhone 15 Pro Max",
                description = "Điện thoại Apple cao cấp, hiệu năng mạnh mẽ.",
                categoryId = "phones",
                sellerId = "BwKxKTBIU8QpUtKY7uzYyQr1Skg2",
                price = 32990000,
                stock = 50,
                soldCount = 120,
                rating = 4.8,
                reviewCount = 230,
                imageUrls = listOf("https://example.com/iphone15-1.jpg"),
                optionGroups = listOf(
                    OptionGroup(id = "color", name = "Màu sắc", values = listOf("Đen", "Trắng", "Xanh")),
                    OptionGroup(id = "storage", name = "Dung lượng", values = listOf("128GB", "256GB", "512GB"))
                ),
                shopLocation = "Hồ Chí Minh"
            ),
            Product(
                id = "samsung-tv",
                name = "Smart TV Samsung 50 inch",
                description = "Smart Tivi 4K độ phân giải cao, điều khiển giọng nói.",
                categoryId = "tv",
                sellerId = "BwKxKTBIU8QpUtKY7uzYyQr1Skg2",
                price = 10490000,
                stock = 30,
                soldCount = 78,
                rating = 4.6,
                reviewCount = 91,
                imageUrls = listOf("https://example.com/samsung-tv.jpg"),
                optionGroups = listOf(
                    OptionGroup(id = "warranty", name = "Bảo hành", values = listOf("12 tháng", "24 tháng"))
                ),
                shopLocation = "Hà Nội"
            ),
            Product(
                id = "air-fryer",
                name = "Nồi chiên không dầu Lock&Lock",
                description = "Dung tích 5L, công suất 1800W, chiên nướng không dầu mỡ.",
                categoryId = "home-appliances",
                sellerId = "BwKxKTBIU8QpUtKY7uzYyQr1Skg2",
                price = 1790000,
                stock = 70,
                soldCount = 210,
                rating = 4.7,
                reviewCount = 300,
                imageUrls = listOf("https://example.com/air-fryer.jpg"),
                shopLocation = "Đà Nẵng"
            ),
            Product(
                id = "macbook-pro",
                name = "MacBook Pro M2 2023",
                description = "Laptop Apple hiệu suất vượt trội, phù hợp dân lập trình và thiết kế.",
                categoryId = "computers",
                sellerId = "BwKxKTBIU8QpUtKY7uzYyQr1Skg2",
                price = 42990000,
                stock = 20,
                soldCount = 45,
                rating = 4.9,
                reviewCount = 88,
                imageUrls = listOf("https://example.com/macbook.jpg"),
                optionGroups = listOf(
                    OptionGroup(id = "storage", name = "Dung lượng", values = listOf("256GB", "512GB", "1TB")),
                    OptionGroup(id = "color", name = "Màu sắc", values = listOf("Xám", "Bạc"))
                ),
                shopLocation = "Hồ Chí Minh"
            ),
            Product(
                id = "baby-milk",
                name = "Sữa bột Vinamilk Optimum Gold 900g",
                description = "Sữa công thức dành cho bé từ 1 đến 2 tuổi.",
                categoryId = "mother-and-baby",
                sellerId = "BwKxKTBIU8QpUtKY7uzYyQr1Skg2",
                price = 475000,
                stock = 150,
                soldCount = 350,
                rating = 4.5,
                reviewCount = 102,
                imageUrls = listOf("https://example.com/vinamilk.jpg"),
                shopLocation = "Cần Thơ"
            ),
            Product(
                id = "gaming-chair",
                name = "Ghế chơi game Warrior WGC202",
                description = "Ghế ngồi thoải mái, thiết kế thể thao, nâng đỡ cột sống.",
                categoryId = "home-and-living",
                sellerId = "BwKxKTBIU8QpUtKY7uzYyQr1Skg2",
                price = 2450000,
                stock = 25,
                soldCount = 60,
                rating = 4.4,
                reviewCount = 37,
                imageUrls = listOf("https://example.com/gaming-chair.jpg"),
                shopLocation = "Hải Phòng"
            ),
            Product(
                id = "wireless-mouse",
                name = "Chuột không dây Logitech M331 Silent",
                description = "Chuột không dây yên tĩnh, dùng pin AA, tiện lợi mang đi.",
                categoryId = "digital-devices",
                sellerId = "BwKxKTBIU8QpUtKY7uzYyQr1Skg2",
                price = 350000,
                stock = 200,
                soldCount = 1000,
                rating = 4.8,
                reviewCount = 780,
                imageUrls = listOf("https://example.com/logitech-m331.jpg"),
                shopLocation = "TP.HCM"
            ),
            Product(
                id = "perfume",
                name = "Nước hoa Dior Sauvage EDT 100ml",
                description = "Hương thơm nam tính, sang trọng, lưu hương lâu.",
                categoryId = "beauty",
                sellerId = "BwKxKTBIU8QpUtKY7uzYyQr1Skg2",
                price = 2890000,
                stock = 40,
                soldCount = 93,
                rating = 4.9,
                reviewCount = 120,
                imageUrls = listOf("https://example.com/dior.jpg"),
                shopLocation = "Hồ Chí Minh"
            ),
            Product(
                id = "bike-helmet",
                name = "Mũ bảo hiểm Andes 3/4",
                description = "Mũ bảo hiểm chuẩn CR, bảo vệ an toàn khi lái xe.",
                categoryId = "automotive",
                sellerId = "BwKxKTBIU8QpUtKY7uzYyQr1Skg2",
                price = 450000,
                stock = 90,
                soldCount = 150,
                rating = 4.3,
                reviewCount = 60,
                imageUrls = listOf("https://example.com/helmet.jpg"),
                shopLocation = "Biên Hòa"
            ),
            Product(
                id = "fitness-watch",
                name = "Đồng hồ thông minh Xiaomi Mi Band 8",
                description = "Theo dõi sức khỏe, đo nhịp tim, chống nước.",
                categoryId = "electronics",
                sellerId = "BwKxKTBIU8QpUtKY7uzYyQr1Skg2",
                price = 790000,
                stock = 80,
                soldCount = 240,
                rating = 4.7,
                reviewCount = 150,
                imageUrls = listOf("https://example.com/miband.jpg"),
                shopLocation = "Thủ Đức"
            )
        )


        products.forEach { product ->
            val collectionRef = db.collection("products")
            for (product in products) {
                collectionRef.document(product.id)
                    .set(product)
                    .addOnSuccessListener {
                        Log.d("FirestoreSeeder", "Đã thêm sản phẩm: ${product.name}")
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirestoreSeeder", "Lỗi khi thêm sản phẩm: ${product.name}", e)
                    }
            }
        }
    }
}
