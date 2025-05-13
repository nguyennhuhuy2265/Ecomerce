package com.example.ecommerce.adapter.seller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.databinding.CommonItemNotificationBinding
import com.example.ecommerce.model.Notification
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationAdapter(private val onClick: (Notification) -> Unit) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    private var notifications = emptyList<Notification>()

    fun submitList(newList: List<Notification>) {
        notifications = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CommonItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int = notifications.size

    inner class ViewHolder(private val binding: CommonItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification) {
            binding.tvNotificationTitle.text = notification.title
            binding.tvNotificationBody.text = notification.body
            binding.tvNotificationTime.text = getTimeAgo(notification.createdAt?.toDate())
            binding.ivNotificationType.setImageResource(R.drawable.ic_cart1)
            binding.tvOrderId.apply {
                if (notification.orderId != null) {
                    text = "Mã đơn hàng: #${notification.orderId.takeLast(6)}"
                    visibility = View.VISIBLE
                } else {
                    visibility = View.GONE
                }
            }
            binding.root.setOnClickListener { onClick(notification) }
        }

        private fun getTimeAgo(date: java.util.Date?): String {
            date ?: return "Chưa có ngày"
            val now = Calendar.getInstance().time
            val diffInMillis = now.time - date.time
            val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
            return when {
                diffInMinutes < 60 -> "$diffInMinutes phút trước"
                diffInMinutes < 1440 -> "${TimeUnit.MILLISECONDS.toHours(diffInMillis)} giờ trước"
                else -> "${TimeUnit.MILLISECONDS.toDays(diffInMillis)} ngày trước"
            }
        }
    }
}