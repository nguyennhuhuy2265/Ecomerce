package com.example.ecommerce.ui.user

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.databinding.UserActivityOrderDetailBinding
import com.example.ecommerce.model.Order
import com.example.ecommerce.model.OrderStatus
import com.example.ecommerce.viewmodel.user.OrderDetailViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class OrderDetailActivity : AppCompatActivity() {

    private lateinit var binding: UserActivityOrderDetailBinding
    private lateinit var viewModel: OrderDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Cấu hình Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false) // Ẩn tiêu đề mặc định của ActionBar

        viewModel = ViewModelProvider(this).get(OrderDetailViewModel::class.java)

        val orderId = intent.getStringExtra("orderId")
        orderId?.let { viewModel.loadOrder(it) }

        setupObservers()
        setupListeners()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Quay lại activity trước đó
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupObservers() {
        viewModel.order.observe(this) { order ->
            order?.let {
                // Header
                binding.tvOrderId.text = "#${it.id.takeLast(6)}"
                binding.tvOrderDate.text = it.createdAt?.toDate()?.let { date ->
                    SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
                } ?: "Chưa có ngày"
                binding.tvOrderStatus.text = when (it.status) {
                    OrderStatus.PENDING -> "Chờ xác nhận"
                    OrderStatus.CONFIRMED -> "Đang chuẩn bị"
                    OrderStatus.SHIPPING -> "Đang giao"
                    OrderStatus.DELIVERED -> "Hoàn thành"
                    OrderStatus.CANCELED -> "Đã hủy"
                }
                binding.tvOrderStatus.backgroundTintList = ContextCompat.getColorStateList(
                    this, when (it.status) {
                        OrderStatus.PENDING -> android.R.color.holo_orange_dark
                        OrderStatus.CONFIRMED -> android.R.color.holo_blue_dark
                        OrderStatus.SHIPPING -> android.R.color.holo_green_dark
                        OrderStatus.DELIVERED -> android.R.color.holo_green_dark
                        OrderStatus.CANCELED -> android.R.color.holo_red_dark
                    }
                )
                binding.tvPaymentStatus.text =
                    if (it.paymentStatus == com.example.ecommerce.model.PaymentStatus.PAID) "Đã thanh toán" else "Chưa thanh toán"
                binding.tvPaymentStatus.backgroundTintList = ContextCompat.getColorStateList(
                    this,
                    if (it.paymentStatus == com.example.ecommerce.model.PaymentStatus.PAID) android.R.color.holo_green_dark else android.R.color.holo_orange_dark
                )

                // Product Info
                binding.tvProductName.text = it.productName
                binding.tvProductOptions.text =
                    if (it.selectedOptions.isNotEmpty()) "Phân loại: ${it.selectedOptions.joinToString()}" else "Không có phân loại"
                binding.tvUnitPrice.text = "₫${it.unitPrice.toInt()}"
                binding.tvQuantity.text = "x${it.quantity}"
                Glide.with(this)
                    .load(it.productImage)
                    .placeholder(R.drawable.ic_load)
                    .into(binding.ivProductImage)

                // Shipping Address
                binding.tvReceiverName.text = viewModel.user.value?.name ?: "Không có tên"
                binding.tvReceiverPhone.text =
                    it.shippingAddress?.phoneNumber ?: "Không có số điện thoại"
                binding.tvShippingAddress.text = it.shippingAddress?.let { address ->
                    "${address.streetNumber} ${address.streetName}, ${address.ward}, ${address.district}, ${address.city}"
                } ?: "Không có địa chỉ"

                // Payment Information
                val productTotal = it.unitPrice * it.quantity
                val shippingFee = 30000.0
                binding.tvProductTotal.text = "₫${productTotal.toInt()}"
                binding.tvShippingFee.text = "₫${shippingFee.toInt()}"
                binding.tvTotalAmount.text = "₫${it.totalAmount.toInt()}"

// Action Buttons
                when (it.status) {
                    OrderStatus.PENDING, OrderStatus.CONFIRMED -> {
                        binding.btnCancelOrder.visibility = View.VISIBLE
                        binding.btnRateProduct.visibility = View.GONE
                        binding.btnRated.visibility = View.GONE
                    }

                    OrderStatus.DELIVERED -> {
                        // Kiểm tra xem đã đánh giá chưa
                        viewModel.hasReviewed.observe(this) { hasReviewed ->
                            if (hasReviewed) {
                                binding.btnCancelOrder.visibility = View.GONE
                                binding.btnRateProduct.visibility = View.GONE
                                binding.btnRated.visibility = View.VISIBLE
                            } else {
                                binding.btnCancelOrder.visibility = View.GONE
                                binding.btnRated.visibility = View.GONE
                                binding.btnRateProduct.visibility = View.VISIBLE
                            }
                        }
                    }

                    else -> {
                        binding.btnCancelOrder.visibility = View.GONE
                        binding.btnRateProduct.visibility = View.GONE
                        binding.btnRated.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnCancelOrder.setOnClickListener {
            viewModel.order.value?.let { order ->
                viewModel.cancelOrder(order.id)
            }
        }

        binding.btnRateProduct.setOnClickListener {
            viewModel.order.value?.let { order ->
                val intent = Intent(this, ReviewActivity::class.java).apply {
                    putExtra("orderId", order.id)
                    putExtra("productId", order.productId)
                }
                startActivity(intent)
            }
        }
    }
}