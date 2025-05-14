package com.example.ecommerce.adapter.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.databinding.UserItemOderBinding
import com.example.ecommerce.model.Order
import kotlin.text.ifEmpty

class OrderAdapter(
    private var orders: List<Order>,
    private val onItemClick: (Order) -> Unit
) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    fun updateOrders(newOrders: List<Order>) {
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

    inner class OrderViewHolder(private val binding: UserItemOderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            Glide.with(binding.root.context)
                .load(order.productImage?.ifEmpty { null } ?: R.drawable.ic_load)
                .placeholder(R.drawable.ic_load)
                .into(binding.imageViewProduct)
            binding.textViewSellerName.text = "Mã đơn hàng: #${order.id}"
            binding.textViewOrderStatus.text = order.status.toString()
            binding.textViewProductName.text = order.productName
            binding.textViewProductOptions.text = order.selectedOptions.joinToString()
            binding.textViewProductPrice.text = "₫${order.unitPrice}"
            binding.textViewQuantity.text = "x${order.quantity}"
            binding.textViewOrderDate.text = order.createdAt.toDate().toString()
            binding.textViewTotalAmount.text = "₫${order.totalAmount}"
            binding.textViewPaymentStatus.text = order.paymentStatus.toString()
            binding.buttonOrderDetail.setOnClickListener { onItemClick(order) }

        }
    }
}