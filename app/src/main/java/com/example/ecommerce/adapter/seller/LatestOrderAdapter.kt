package com.example.ecommerce.adapter.seller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.databinding.SellerItemLatestOrderBinding
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderStatus
import java.util.Calendar
import java.util.concurrent.TimeUnit

class LatestOrderAdapter(private val onOrderClick: (Order) -> Unit) : RecyclerView.Adapter<LatestOrderAdapter.OrderViewHolder>() {

    private var orders = emptyList<Order>()

    fun submitList(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
        println("LatestOrderAdapter updated with ${orders.size} items")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = SellerItemLatestOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
        println("Binding item at position $position: ${orders[position].id}")
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(private val binding: SellerItemLatestOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            println("Binding order: ${order.id}")
            // Hiển thị ảnh sản phẩm
            Glide.with(binding.ivOrderImage.context)
                .load(order.productImage)
                .placeholder(R.drawable.ic_load)
                .into(binding.ivOrderImage)

            // Hiển thị mã đơn hàng
            binding.tvOrderId.text = "Đơn #${order.id.takeLast(6)}"

            // Hiển thị tóm tắt: số sản phẩm và tổng tiền
            binding.tvOrderSummary.text = "1 sản phẩm | ₫${order.totalAmount.toInt()}"

            // Hiển thị thời gian (tính từ createdAt)
            binding.tvOrderTime.text = getTimeAgo(order.createdAt?.toDate())

            // Hiển thị trạng thái
            binding.tvOrderStatus.text = when (order.status) {
                OrderStatus.PENDING -> "Chờ xác nhận"
                OrderStatus.CONFIRMED -> "Đang chuẩn bị"
                OrderStatus.SHIPPING -> "Đang giao"
                OrderStatus.DELIVERED -> "Hoàn thành"
                OrderStatus.CANCELED -> "Đã hủy"
            }
            binding.tvOrderStatus.setTextColor(ContextCompat.getColor(binding.root.context, when (order.status) {
                OrderStatus.PENDING -> android.R.color.holo_orange_dark
                OrderStatus.CONFIRMED -> android.R.color.holo_blue_dark
                OrderStatus.SHIPPING -> android.R.color.holo_green_dark
                OrderStatus.DELIVERED -> android.R.color.holo_green_dark
                OrderStatus.CANCELED -> android.R.color.holo_red_dark
            }))

            // Thêm onclick để chuyển sang OrderDetailActivity
            binding.root.setOnClickListener { onOrderClick(order) }
        }

        private fun getTimeAgo(date: java.util.Date?): String {
            date ?: return "Chưa có ngày"
            val now = Calendar.getInstance().time
            val diffInMillis = now.time - date.time
            val diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
            return when {
                diffInMinutes < 60 -> "${diffInMinutes} phút trước"
                diffInMinutes < 1440 -> "${TimeUnit.MILLISECONDS.toHours(diffInMillis)} giờ trước"
                else -> "${TimeUnit.MILLISECONDS.toDays(diffInMillis)} ngày trước"
            }
        }
    }
}