package com.example.ecommerce.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val id: String = "", // ID của tin nhắn
    val senderId: String = "", // ID người gửi
    val content: String = "", // Nội dung (text hoặc URL ảnh)
    val type: MessageType = MessageType.TEXT, // Loại tin nhắn
    val timestamp: Timestamp = Timestamp.now(), // Thời gian gửi
    val isRead: Boolean = false // Trạng thái đã đọc
) : Parcelable

enum class MessageType {
    TEXT, IMAGE
}

@Parcelize
data class Conversation(
    val id: String = "", // ID cuộc trò chuyện (userId_sellerId)
    val userId: String = "",
    val sellerId: String = "",
    val userName: String = "", // Tên người mua (dùng để hiển thị preview)
    val sellerName: String = "", // Tên người bán (dùng để hiển thị preview)
    val lastMessage: Message? = null, // Tin nhắn cuối cùng (dùng để hiển thị preview)
    val lastUpdated: Timestamp = Timestamp.now() // Thời gian cập nhật cuối
) : Parcelable