package com.example.ecommerce.ui.seller

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.databinding.SellerActivityOderDetailBinding
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.model.PaymentStatus
import com.example.ecommerce.viewmodel.seller.OrderDetailViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: SellerActivityOderDetailBinding
    private lateinit var viewModel: OrderDetailViewModel
    private var orderId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SellerActivityOderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        orderId = intent.getStringExtra("orderId") ?: run {
            Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel = ViewModelProvider(this).get(OrderDetailViewModel::class.java)

        setupToolbar()
        setupObservers()
        setupListeners()
        viewModel.loadOrder(orderId!!)
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupObservers() {
        viewModel.order.observe(this) { order ->
            order?.let { displayOrderDetails(it) }
        }

        viewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }

        viewModel.updateResult.observe(this) { result ->
            result?.let { Toast.makeText(this, "Cập nhật trạng thái thành công", Toast.LENGTH_SHORT).show() }
        }
    }

    private fun setupListeners() {

        binding.btnConfirm.setOnClickListener {
            viewModel.updateOrderStatus(orderId!!, OrderStatus.CONFIRMED)
        }

        binding.btnShipping.setOnClickListener {
            viewModel.updateOrderStatus(orderId!!, OrderStatus.SHIPPING)
        }

        binding.btnCancel.setOnClickListener {
            viewModel.updateOrderStatus(orderId!!, OrderStatus.CANCELED)
        }
    }

    private fun displayOrderDetails(order: Order) {
        binding.tvOrderId.text = order.id
        binding.tvOrderDate.text = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(order.createdAt.toDate())
        binding.tvProductName.text = order.productName
        binding.tvOptions.text = if (order.selectedOptions.isNotEmpty()) {
            "Phân loại: ${order.selectedOptions.joinToString(", ")}"
        } else {
            "Không có tùy chọn"
        }
        binding.tvUnitPrice.text = "₫${order.unitPrice.toInt()}"
        binding.tvQuantity.text = order.quantity.toString()
        binding.tvTotalAmount.text = "₫${order.totalAmount.toInt()}"
        binding.tvPaymentStatus.text = when (order.paymentStatus) {
            PaymentStatus.PENDING -> "Chưa thanh toán"
            PaymentStatus.PAID -> "Đã thanh toán"
        }
        binding.tvShippingAddress.text = order.shippingAddress?.let {
            "${it.streetNumber} ${it.streetName}, ${it.ward}, ${it.district}, ${it.city}, ${it.country}"
        } ?: "Không có địa chỉ"

        Glide.with(this)
            .load(order.productImage)
            .placeholder(R.drawable.ic_load)
            .into(binding.ivProductImage)

        binding.tvOrderStatus.text = when (order.status) {
            OrderStatus.PENDING -> "Chờ xác nhận"
            OrderStatus.CONFIRMED -> "Đang chuẩn bị"
            OrderStatus.SHIPPING -> "Đang giao"
            OrderStatus.DELIVERED -> "Hoàn thành"
            OrderStatus.CANCELED -> "Đã hủy"
        }

        binding.tvOrderStatus.setTextColor(ContextCompat.getColor(this, when (order.status) {
            OrderStatus.PENDING -> android.R.color.holo_orange_dark
            OrderStatus.CONFIRMED -> android.R.color.holo_blue_dark
            OrderStatus.SHIPPING -> android.R.color.holo_green_dark
            OrderStatus.DELIVERED -> android.R.color.holo_green_dark
            OrderStatus.CANCELED -> android.R.color.holo_red_dark
        }))

        updateButtonVisibility(order.status)
    }

    private fun updateButtonVisibility(status: OrderStatus) {
        binding.btnConfirm.visibility = if (status == OrderStatus.PENDING) View.VISIBLE else View.GONE
        binding.btnShipping.visibility = if (status == OrderStatus.CONFIRMED) View.VISIBLE else View.GONE
        binding.btnCancel.visibility = if (status in listOf(OrderStatus.PENDING)) View.VISIBLE else View.GONE
    }
}