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

        // Generate Products
        generateProducts()

//        // Generate Banners
//        generateBanners()
    }

    private fun generateUsers() {
        val users = listOf(
            User(
                id = "user1",
                name = "Nguyen Van A",
                email = "vana@example.com",
                phoneNumber = "0901234567",
                role = "user",
                recommendedProductIds = listOf("prod1", "prod2"),
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            User(
                id = "user2",
                name = "Tran Thi B",
                email = "thib@example.com",
                phoneNumber = "0912345678",
                role = "user",
                recommendedProductIds = listOf("prod2", "prod3"),
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            User(
                id = "seller1",
                name = "Cửa hàng Apple Việt Nam",
                email = "applevn@example.com",
                phoneNumber = "02812345678",
                address = Address(
                    streetNumber = "123",
                    streetName = "Le Loi",
                    ward = "Phuong 1",
                    district = "Quan 1",
                    city = "Ho Chi Minh",
                    country = "Vietnam"
                ),
                role = "seller",
                shopName = "Apple Store VN",
                shopCategory = "Điện tử",
                rating = 4.8,
                reviewCount = 150,
                isVerified = true,
                businessHours = mapOf(
                    "Monday" to "9:00-18:00",
                    "Tuesday" to "9:00-18:00",
                    "Wednesday" to "9:00-18:00",
                    "Thursday" to "9:00-18:00",
                    "Friday" to "9:00-18:00",
                    "Saturday" to "10:00-17:00",
                    "Sunday" to "Closed"
                ),
                bannerUrl = "https://example.com/seller1_banner.jpg",
                followersCount = 1000,
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            User(
                id = "seller2",
                name = "Shop Phụ kiện Hà Nội",
                email = "shophn@example.com",
                phoneNumber = "02498765432",
                address = Address(
                    streetNumber = "456",
                    streetName = "Tran Phu",
                    ward = "Phuong 2",
                    district = "Quan Ba Dinh",
                    city = "Ha Noi",
                    country = "Vietnam"
                ),
                role = "seller",
                shopName = "Phụ kiện 24/7",
                shopCategory = "Phụ kiện",
                rating = 4.5,
                reviewCount = 80,
                isVerified = true,
                businessHours = mapOf(
                    "Monday" to "8:00-20:00",
                    "Tuesday" to "8:00-20:00",
                    "Wednesday" to "8:00-20:00",
                    "Thursday" to "8:00-20:00",
                    "Friday" to "8:00-20:00",
                    "Saturday" to "9:00-18:00",
                    "Sunday" to "9:00-18:00"
                ),
                bannerUrl = "https://example.com/seller2_banner.jpg",
                followersCount = 500,
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            )
        )

        users.forEach { user ->
            db.collection("users")
                .document(user.id)
                .set(user)
                .addOnSuccessListener { println("User ${user.id} added successfully") }
                .addOnFailureListener { e -> println("Error adding user ${user.id}: $e") }
        }
    }

    private fun generateCategories() {
        val categories = listOf(
            Category(
                id = "cat1",
                name = "Điện thoại",
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            Category(
                id = "cat2",
                name = "Phụ kiện",
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            Category(
                id = "cat3",
                name = "Ốp lưng",
                parentCategoryId = "cat2",
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            Category(
                id = "cat4",
                name = "Tai nghe",
                parentCategoryId = "cat2",
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            Category(
                id = "cat5",
                name = "Máy tính xách tay",
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            )
        )

        categories.forEach { category ->
            db.collection("categories")
                .document(category.id)
                .set(category)
                .addOnSuccessListener { println("Category ${category.id} added successfully") }
                .addOnFailureListener { e -> println("Error adding category ${category.id}: $e") }
        }
    }

    private fun generateProducts() {
        val products = listOf(
//            Product(
//                id = "prod1",
//                name = "iPhone 14",
//                description = "Điện thoại cao cấp từ Apple với camera 48MP.",
//                categoryId = "cat1",
//                sellerId = "seller1",
//                price = 20000000,
//                stock = 50,
//                soldCount = 10,
//                avgRating = 4.5,
//                reviewCount = 20,
//                imageUrls = listOf("https://example.com/iphone14_1.jpg", "https://example.com/iphone14_2.jpg"),
//                defaultImageUrl = "https://example.com/iphone14_1.jpg",
//                optionGroups = listOf(
//                    OptionGroup(
//                        id = "opt1",
//                        name = "Dung lượng",
//                        values = listOf(
//                            OptionValue(id = "val1", displayName = "128GB", extraPrice = 0.0, isDefault = true, stock = 30),
//                            OptionValue(id = "val2", displayName = "256GB", extraPrice = 3000000.0, isDefault = false, stock = 20)
//                        ),
//                        isRequired = true,
//                        priority = 1
//                    ),
//                    OptionGroup(
//                        id = "opt2",
//                        name = "Màu sắc",
//                        values = listOf(
//                            OptionValue(id = "val3", displayName = "Đen", extraPrice = 0.0, isDefault = true, stock = 25),
//                            OptionValue(id = "val4", displayName = "Trắng", extraPrice = 0.0, isDefault = false, stock = 25)
//                        ),
//                        isRequired = false,
//                        priority = 2
//                    )
//                ),
//                createdAt = currentTimestamp,
//                updatedAt = currentTimestamp
//            ),
//            Product(
//                id = "prod2",
//                name = "Ốp lưng iPhone 14 Silicon",
//                description = "Ốp lưng chất liệu silicon cao cấp, bảo vệ tốt.",
//                categoryId = "cat3",
//                sellerId = "seller2",
//                price = 250000,
//                stock = 100,
//                soldCount = 30,
//                avgRating = 4.2,
//                reviewCount = 15,
//                imageUrls = listOf("https://example.com/case_1.jpg", "https://example.com/case_2.jpg"),
//                defaultImageUrl = "https://example.com/case_1.jpg",
//                optionGroups = listOf(
//                    OptionGroup(
//                        id = "opt3",
//                        name = "Màu sắc",
//                        values = listOf(
//                            OptionValue(id = "val5", displayName = "Đen", extraPrice = 0.0, isDefault = true, stock = 50),
//                            OptionValue(id = "val6", displayName = "Xanh", extraPrice = 0.0, isDefault = false, stock = 50)
//                        ),
//                        isRequired = true,
//                        priority = 1
//                    )
//                ),
//                createdAt = currentTimestamp,
//                updatedAt = currentTimestamp
//            ),
//            Product(
//                id = "prod3",
//                name = "MacBook Air M2",
//                description = "Laptop siêu nhẹ với chip M2, hiệu năng vượt trội.",
//                categoryId = "cat5",
//                sellerId = "seller1",
//                price = 30000000,
//                stock = 20,
//                soldCount = 5,
//                avgRating = 4.7,
//                reviewCount = 10,
//                imageUrls = listOf("https://example.com/macbook_1.jpg", "https://example.com/macbook_2.jpg"),
//                defaultImageUrl = "https://example.com/macbook_1.jpg",
//                optionGroups = listOf(
//                    OptionGroup(
//                        id = "opt4",
//                        name = "Dung lượng",
//                        values = listOf(
//                            OptionValue(id = "val7", displayName = "256GB", extraPrice = 0.0, isDefault = true, stock = 15),
//                            OptionValue(id = "val8", displayName = "512GB", extraPrice = 5000000.0, isDefault = false, stock = 5)
//                        ),
//                        isRequired = true,
//                        priority = 1
//                    )
//                ),
//                createdAt = currentTimestamp,
//                updatedAt = currentTimestamp
//            ),
            // Thêm 10 sản phẩm mới
            Product(
                id = "prod4",
                name = "Samsung Galaxy S23",
                description = "Điện thoại flagship với camera 50MP.",
                categoryId = "cat1",
                sellerId = "seller1",
                price = 18000000,
                stock = 40,
                soldCount = 15,
                avgRating = 4.6,
                reviewCount = 25,
                imageUrls = listOf("https://example.com/s23_1.jpg", "https://example.com/s23_2.jpg"),
                defaultImageUrl = "https://example.com/s23_1.jpg",
                optionGroups = listOf(
                    OptionGroup(
                        id = "opt5",
                        name = "Dung lượng",
                        values = listOf(
                            OptionValue(id = "val9", displayName = "128GB", extraPrice = 0.0, isDefault = true, stock = 25),
                            OptionValue(id = "val10", displayName = "256GB", extraPrice = 2500000.0, isDefault = false, stock = 15)
                        ),
                        isRequired = true,
                        priority = 1
                    ),
                    OptionGroup(
                        id = "opt6",
                        name = "Màu sắc",
                        values = listOf(
                            OptionValue(id = "val11", displayName = "Xanh", extraPrice = 0.0, isDefault = true, stock = 20),
                            OptionValue(id = "val12", displayName = "Đen", extraPrice = 0.0, isDefault = false, stock = 20)
                        ),
                        isRequired = false,
                        priority = 2
                    )
                ),
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            Product(
                id = "prod5",
                name = "AirPods Pro 2",
                description = "Tai nghe không dây với chống ồn chủ động.",
                categoryId = "cat4",
                sellerId = "seller2",
                price = 6000000,
                stock = 80,
                soldCount = 40,
                avgRating = 4.8,
                reviewCount = 30,
                imageUrls = listOf("https://example.com/airpods_1.jpg", "https://example.com/airpods_2.jpg"),
                defaultImageUrl = "https://example.com/airpods_1.jpg",
                optionGroups = listOf(
                    OptionGroup(
                        id = "opt7",
                        name = "Màu sắc",
                        values = listOf(
                            OptionValue(id = "val13", displayName = "Trắng", extraPrice = 0.0, isDefault = true, stock = 40),
                            OptionValue(id = "val14", displayName = "Đen", extraPrice = 0.0, isDefault = false, stock = 40)
                        ),
                        isRequired = true,
                        priority = 1
                    )
                ),
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            Product(
                id = "prod6",
                name = "Ốp lưng Samsung S23",
                description = "Ốp lưng cứng cáp cho Samsung S23.",
                categoryId = "cat3",
                sellerId = "seller2",
                price = 300000,
                stock = 120,
                soldCount = 50,
                avgRating = 4.3,
                reviewCount = 20,
                imageUrls = listOf("https://example.com/s23case_1.jpg", "https://example.com/s23case_2.jpg"),
                defaultImageUrl = "https://example.com/s23case_1.jpg",
                optionGroups = listOf(
                    OptionGroup(
                        id = "opt8",
                        name = "Màu sắc",
                        values = listOf(
                            OptionValue(id = "val15", displayName = "Đen", extraPrice = 0.0, isDefault = true, stock = 60),
                            OptionValue(id = "val16", displayName = "Xám", extraPrice = 0.0, isDefault = false, stock = 60)
                        ),
                        isRequired = true,
                        priority = 1
                    )
                ),
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            Product(
                id = "prod7",
                name = "Dell XPS 13",
                description = "Laptop mỏng nhẹ, hiệu năng cao.",
                categoryId = "cat5",
                sellerId = "seller1",
                price = 28000000,
                stock = 15,
                soldCount = 8,
                avgRating = 4.6,
                reviewCount = 12,
                imageUrls = listOf("https://example.com/xps13_1.jpg", "https://example.com/xps13_2.jpg"),
                defaultImageUrl = "https://example.com/xps13_1.jpg",
                optionGroups = listOf(
                    OptionGroup(
                        id = "opt9",
                        name = "Dung lượng",
                        values = listOf(
                            OptionValue(id = "val17", displayName = "512GB", extraPrice = 0.0, isDefault = true, stock = 10),
                            OptionValue(id = "val18", displayName = "1TB", extraPrice = 4000000.0, isDefault = false, stock = 5)
                        ),
                        isRequired = true,
                        priority = 1
                    )
                ),
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            Product(
                id = "prod8",
                name = "Tai nghe Sony WH-1000XM5",
                description = "Tai nghe chống ồn tốt nhất 2023.",
                categoryId = "cat4",
                sellerId = "seller2",
                price = 8000000,
                stock = 60,
                soldCount = 25,
                avgRating = 4.9,
                reviewCount = 35,
                imageUrls = listOf("https://example.com/sony_1.jpg", "https://example.com/sony_2.jpg"),
                defaultImageUrl = "https://example.com/sony_1.jpg",
                optionGroups = listOf(
                    OptionGroup(
                        id = "opt10",
                        name = "Màu sắc",
                        values = listOf(
                            OptionValue(id = "val19", displayName = "Đen", extraPrice = 0.0, isDefault = true, stock = 30),
                            OptionValue(id = "val20", displayName = "Bạc", extraPrice = 0.0, isDefault = false, stock = 30)
                        ),
                        isRequired = true,
                        priority = 1
                    )
                ),
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            Product(
                id = "prod9",
                name = "Cáp sạc Type-C",
                description = "Cáp sạc nhanh, độ bền cao.",
                categoryId = "cat2",
                sellerId = "seller2",
                price = 150000,
                stock = 200,
                soldCount = 100,
                avgRating = 4.4,
                reviewCount = 50,
                imageUrls = listOf("https://example.com/cable_1.jpg", "https://example.com/cable_2.jpg"),
                defaultImageUrl = "https://example.com/cable_1.jpg",
                optionGroups = listOf(
                    OptionGroup(
                        id = "opt11",
                        name = "Độ dài",
                        values = listOf(
                            OptionValue(id = "val21", displayName = "1m", extraPrice = 0.0, isDefault = true, stock = 100),
                            OptionValue(id = "val22", displayName = "2m", extraPrice = 50000.0, isDefault = false, stock = 100)
                        ),
                        isRequired = true,
                        priority = 1
                    )
                ),
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            Product(
                id = "prod10",
                name = "iPad Air 5",
                description = "Máy tính bảng hiệu năng mạnh mẽ.",
                categoryId = "cat1",
                sellerId = "seller1",
                price = 15000000,
                stock = 30,
                soldCount = 12,
                avgRating = 4.7,
                reviewCount = 18,
                imageUrls = listOf("https://example.com/ipad_1.jpg", "https://example.com/ipad_2.jpg"),
                defaultImageUrl = "https://example.com/ipad_1.jpg",
                optionGroups = listOf(
                    OptionGroup(
                        id = "opt12",
                        name = "Dung lượng",
                        values = listOf(
                            OptionValue(id = "val23", displayName = "64GB", extraPrice = 0.0, isDefault = true, stock = 20),
                            OptionValue(id = "val24", displayName = "256GB", extraPrice = 4000000.0, isDefault = false, stock = 10)
                        ),
                        isRequired = true,
                        priority = 1
                    )
                ),
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            Product(
                id = "prod11",
                name = "Chuột không dây Logitech MX Master 3",
                description = "Chuột văn phòng cao cấp.",
                categoryId = "cat2",
                sellerId = "seller1",
                price = 2000000,
                stock = 70,
                soldCount = 35,
                avgRating = 4.6,
                reviewCount = 28,
                imageUrls = listOf("https://example.com/mouse_1.jpg", "https://example.com/mouse_2.jpg"),
                defaultImageUrl = "https://example.com/mouse_1.jpg",
                optionGroups = listOf(
                    OptionGroup(
                        id = "opt13",
                        name = "Màu sắc",
                        values = listOf(
                            OptionValue(id = "val25", displayName = "Đen", extraPrice = 0.0, isDefault = true, stock = 35),
                            OptionValue(id = "val26", displayName = "Xám", extraPrice = 0.0, isDefault = false, stock = 35)
                        ),
                        isRequired = true,
                        priority = 1
                    )
                ),
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            Product(
                id = "prod12",
                name = "Bàn phím cơ Keychron K8",
                description = "Bàn phím cơ chất lượng cao.",
                categoryId = "cat2",
                sellerId = "seller2",
                price = 2500000,
                stock = 50,
                soldCount = 20,
                avgRating = 4.7,
                reviewCount = 15,
                imageUrls = listOf("https://example.com/keyboard_1.jpg", "https://example.com/keyboard_2.jpg"),
                defaultImageUrl = "https://example.com/keyboard_1.jpg",
                optionGroups = listOf(
                    OptionGroup(
                        id = "opt14",
                        name = "Switch",
                        values = listOf(
                            OptionValue(id = "val27", displayName = "Red", extraPrice = 0.0, isDefault = true, stock = 25),
                            OptionValue(id = "val28", displayName = "Brown", extraPrice = 100000.0, isDefault = false, stock = 25)
                        ),
                        isRequired = true,
                        priority = 1
                    )
                ),
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            ),
            Product(
                id = "prod13",
                name = "Apple Watch Series 8",
                description = "Đồng hồ thông minh với theo dõi sức khỏe.",
                categoryId = "cat1",
                sellerId = "seller1",
                price = 12000000,
                stock = 25,
                soldCount = 10,
                avgRating = 4.8,
                reviewCount = 22,
                imageUrls = listOf("https://example.com/watch_1.jpg", "https://example.com/watch_2.jpg"),
                defaultImageUrl = "https://example.com/watch_1.jpg",
                optionGroups = listOf(
                    OptionGroup(
                        id = "opt15",
                        name = "Kích thước",
                        values = listOf(
                            OptionValue(id = "val29", displayName = "41mm", extraPrice = 0.0, isDefault = true, stock = 15),
                            OptionValue(id = "val30", displayName = "45mm", extraPrice = 1000000.0, isDefault = false, stock = 10)
                        ),
                        isRequired = true,
                        priority = 1
                    )
                ),
                createdAt = currentTimestamp,
                updatedAt = currentTimestamp
            )
        )

        products.forEach { product ->
            db.collection("products")
                .document(product.id)
                .set(product)
                .addOnSuccessListener { println("Product ${product.id} added successfully") }
                .addOnFailureListener { e -> println("Error adding product ${product.id}: $e") }
        }
    }

    private fun generateBanners() {
        val banners = listOf(
            Banner(
                id = "banner1",
                imageUrl = "https://example.com/banner1.jpg",
                createdAt = currentTimestamp
            ),
            Banner(
                id = "banner2",
                imageUrl = "https://example.com/banner2.jpg",
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