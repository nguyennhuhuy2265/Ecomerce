package com.example.ecommerce.ui.admin

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.adapter.admin.AdminProductAdapter
import com.example.ecommerce.viewmodel.admin.AdminViewModel
import com.example.ecommerce.databinding.AdminSellerProductsActivityBinding

class AdminSellerProductsActivity : AppCompatActivity() {
    private lateinit var binding: AdminSellerProductsActivityBinding
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var productAdapter: AdminProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminSellerProductsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy sellerId và sellerName từ Intent
        val sellerId = intent.getStringExtra("SELLER_ID") ?: return
        val sellerName = intent.getStringExtra("SELLER_NAME") ?: "Seller"

        // Thiết lập tiêu đề
        title = "Sản phẩm của $sellerName"

        // Khởi tạo RecyclerView
        productAdapter = AdminProductAdapter { productId ->
            viewModel.deleteProduct(productId)
        }
        binding.recyclerViewProducts.apply {
            layoutManager = LinearLayoutManager(this@AdminSellerProductsActivity)
            adapter = productAdapter
        }

        // Quan sát danh sách sản phẩm
        viewModel.products.observe(this) { products ->
            productAdapter.submitList(products)
        }

        // Quan sát kết quả thao tác
        viewModel.operationResult.observe(this) { success ->
            Toast.makeText(this, if (success) "Xóa sản phẩm thành công" else "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show()
            if (success) viewModel.fetchProductsBySeller(sellerId)
        }

        // Lấy danh sách sản phẩm
        viewModel.fetchProductsBySeller(sellerId)
    }
}