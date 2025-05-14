package com.example.ecommerce.ui.user

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.ecommerce.databinding.UserActivityOrderDetailBinding
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.viewmodel.user.OrderDetailViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: UserActivityOrderDetailBinding
    private lateinit var viewModel: OrderDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(OrderDetailViewModel::class.java)

        val orderId = intent.getStringExtra("orderId")
        orderId?.let { viewModel.loadOrder(it) }

        setupObservers()
    }

    private fun setupObservers() {
        viewModel.order.observe(this) { order ->
            order?.let {
                binding.textViewOrderId.text = "Mã đơn: #${order.id.takeLast(6)}"
                binding.textViewOrderStatus.text = when (order.status) {
                    OrderStatus.PENDING -> "Chờ xác nhận"
                    OrderStatus.CONFIRMED -> "Đang chuẩn bị"
                    OrderStatus.SHIPPING -> "Đang giao"
                    OrderStatus.DELIVERED -> "Hoàn thành"
                    OrderStatus.CANCELED -> "Đã hủy"
                }
                binding.textViewProductName.text = order.productName
                binding.textViewProductOptions.text = order.selectedOptions.joinToString()
                binding.textViewProductPrice.text = "₫${order.unitPrice.toInt()}"
                binding.textViewQuantity.text = "x${order.quantity}"
                binding.textViewTotalAmount.text = "₫${order.totalAmount.toInt()}"
                binding.textViewOrderDate.text = order.createdAt?.toDate()?.let {
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                } ?: "Chưa có ngày"
                binding.textViewPaymentStatus.text = order.paymentStatus.toString()
            }
        }
    }
}