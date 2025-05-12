package com.example.ecommerce

import android.util.Log
import com.example.ecommerce.model.Review
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

object ReviewUploader {

    private val db = FirebaseFirestore.getInstance()
    private const val TAG = "ReviewUploader"

    fun uploadSampleReviews() {
        val reviews = listOf(
            Review(
                id = UUID.randomUUID().toString(),
                productId = "4km4pmZacfuP2n70U1BH",
                userId = "user1",
                userName = "Nguyễn Văn A",
                userAvatarUrl = "https://example.com/avatar1.jpg",
                rating = 5,
                comment = "Sản phẩm rất tốt, đúng mô tả và giao hàng nhanh.",
                createdAt = Timestamp.now()
            ),
            Review(
                id = UUID.randomUUID().toString(),
                productId = "4km4pmZacfuP2n70U1BH",
                userId = "user2",
                userName = "Trần Thị B",
                userAvatarUrl = "https://example.com/avatar2.jpg",
                rating = 4,
                comment = "Chất lượng ổn so với giá, sẽ ủng hộ lần sau.",
                createdAt = Timestamp.now()
            ),
            Review(
                id = UUID.randomUUID().toString(),
                productId = "4km4pmZacfuP2n70U1BH",
                userId = "user3",
                userName = "Lê Văn C",
                userAvatarUrl = "https://example.com/avatar3.jpg",
                rating = 5,
                comment = "Đóng gói cẩn thận, sản phẩm không bị lỗi.",
                createdAt = Timestamp.now()
            ),
            Review(
                id = UUID.randomUUID().toString(),
                productId = "4km4pmZacfuP2n70U1BH",
                userId = "user4",
                userName = "Phạm Thị D",
                userAvatarUrl = "https://example.com/avatar4.jpg",
                rating = 3,
                comment = "Sản phẩm dùng ổn, nhưng giao hàng hơi lâu.",
                createdAt = Timestamp.now()
            ),
            Review(
                id = UUID.randomUUID().toString(),
                productId = "4km4pmZacfuP2n70U1BH",
                userId = "user5",
                userName = "Đỗ Văn E",
                userAvatarUrl = "https://example.com/avatar5.jpg",
                rating = 4,
                comment = "Mọi thứ đều ok, sẽ giới thiệu cho bạn bè.",
                createdAt = Timestamp.now()
            )
        )

        val collectionRef = db.collection("reviews")
        reviews.forEach { review ->
            collectionRef.document(review.id)
                .set(review)
                .addOnSuccessListener {
                    Log.d(TAG, "Đã thêm review của ${review.userName}")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Lỗi khi thêm review: ${review.userName}", e)
                }
        }
    }
}
