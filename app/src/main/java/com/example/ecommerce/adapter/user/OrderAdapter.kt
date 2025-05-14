package com.example.ecommerce.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.databinding.UserItemOderBinding
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.model.PaymentStatus
import java.text.SimpleDateFormat
import java.util.Locale

class OrderAdapter(private val onOrderClick: (Order) -> Unit) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private var orders = emptyList<Order>()

    fun submitList(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = UserItemOderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position])
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(private val binding: UserItemOderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.textViewOrderId.text = "Mã đơn: #${order.id.takeLast(6)}"
            binding.textViewOrderStatus.text = when (order.status) {
                OrderStatus.PENDING -> "Chờ xác nhận"
                OrderStatus.CONFIRMED -> "Đang chuẩn bị"
                OrderStatus.SHIPPING -> "Đang giao"
                OrderStatus.DELIVERED -> "Hoàn thành"
                OrderStatus.CANCELED -> "Đã hủy"
            }
            binding.textViewOrderStatus.setTextColor(ContextCompat.getColor(binding.root.context, when (order.status) {
                OrderStatus.PENDING -> android.R.color.holo_orange_dark
                OrderStatus.CONFIRMED -> android.R.color.holo_blue_dark
                OrderStatus.SHIPPING -> android.R.color.holo_green_dark
                OrderStatus.DELIVERED -> android.R.color.holo_green_dark
                OrderStatus.CANCELED -> android.R.color.holo_red_dark
            }))
            binding.textViewProductName.text = order.productName
            binding.textViewProductOptions.text = if (order.selectedOptions.isNotEmpty()) {
                "Phân loại: ${order.selectedOptions.joinToString(", ")}"
            } else {
                "Không có tùy chọn"
            }
            binding.textViewPaymentStatus.text = when (order.paymentStatus) {
                PaymentStatus.PENDING -> "Chưa thanh toán"
                PaymentStatus.PAID -> "Đã thanh toán"
            }
            binding.textViewPaymentStatus.setTextColor(ContextCompat.getColor(binding.root.context, when (order.paymentStatus) {
                PaymentStatus.PENDING -> android.R.color.holo_orange_dark
                PaymentStatus.PAID -> android.R.color.holo_green_dark
            }))
            binding.textViewProductPrice.text = "₫${order.unitPrice.toInt()}"
            binding.textViewQuantity.text = "x${order.quantity}"
            binding.textViewTotalAmount.text = "₫${order.totalAmount.toInt()}"
            binding.textViewOrderDate.text = order.createdAt?.toDate()?.let {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
            } ?: "Chưa có ngày"

            Glide.with(binding.imageViewProduct.context)
                .load(order.productImage)
                .placeholder(R.drawable.ic_load)
                .into(binding.imageViewProduct)

            binding.buttonOrderDetail.setOnClickListener { onOrderClick(order) }
        }
    }
}