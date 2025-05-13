package com.example.ecommerce.viewmodel.seller

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.model.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NotificationViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val notificationsCollection = db.collection("notifications")

    private val _unreadNotifications = MutableLiveData<List<Notification>>()
    val unreadNotifications: LiveData<List<Notification>> get() = _unreadNotifications

    private val _readNotifications = MutableLiveData<List<Notification>>()
    val readNotifications: LiveData<List<Notification>> get() = _readNotifications

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun loadNotifications(userId: String) {
        viewModelScope.launch {
            try {
                val snapshot = notificationsCollection
                    .whereEqualTo("userId", userId)
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
                val notifications = snapshot.documents.mapNotNull { it.toObject(Notification::class.java) }
                _unreadNotifications.value = notifications.filter { !it.read }
                _readNotifications.value = notifications.filter { it.read }
            } catch (e: Exception) {
                _error.value = e.message ?: "Lỗi khi tải thông báo"
            }
        }
    }

    fun markAsRead(notification: Notification) {
        viewModelScope.launch {
            try {
                if (!notification.read) {
                    notificationsCollection
                        .document(notification.id)
                        .update("read", true)
                        .await()
                    _unreadNotifications.value = _unreadNotifications.value?.filter { it.id != notification.id }
                    _readNotifications.value = (_readNotifications.value?.toMutableList() ?: mutableListOf()).apply {
                        add(notification.copy(read = true))
                    }.sortedByDescending { it.createdAt?.toDate() }
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Lỗi khi cập nhật trạng thái đọc"
            }
        }
    }
}