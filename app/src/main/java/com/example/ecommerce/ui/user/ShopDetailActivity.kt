package com.example.ecommerce.ui.user

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.ecommerce.R
import com.example.ecommerce.adapter.user.ProductAdapter
import com.example.ecommerce.databinding.UserActivityShopDetailBinding
import com.example.ecommerce.model.Product
import com.example.ecommerce.viewmodel.user.ShopDetailViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class ShopDetailActivity : AppCompatActivity() {
    private lateinit var binding: UserActivityShopDetailBinding
    private lateinit var viewModel: ShopDetailViewModel
    private lateinit var productAdapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserActivityShopDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ShopDetailViewModel::class.java)

        // Lấy sellerId từ Intent
        val sellerId = intent.getStringExtra("sellerId") ?: ""
        viewModel.loadShopDetails(sellerId)

        setupToolbar()
        setupRecyclerView()
        setupObservers()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter(emptyList()) { product ->
            val intent = Intent(this, ProductDetailActivity::class.java).apply {
                putExtra("product", product)
            }
            startActivity(intent)
        }
        binding.rvProducts.layoutManager = GridLayoutManager(this, 2)
        binding.rvProducts.adapter = productAdapter
    }

    private fun setupObservers() {
        viewModel.seller.observe(this) { seller ->
            seller?.let {
                binding.tvShopName.text = it.shopName ?: it.name
                binding.tvShopCategory.text = "Danh mục: ${it.shopCategory ?: "Không xác định"}"
                binding.tvShopRating.text = it.rating.toString()
                binding.tvReviewCount.text = "(${it.reviewCount} lượt)"
                binding.tvProductCount.text = viewModel.products.value?.size?.toString() ?: "0"
                binding.tvFollowerCount.text = "5.2k" // Giả lập, có thể thêm logic lấy follower
                binding.tvSoldCount.text = it.soldCount.toString()
                binding.tvShopLocation.text = it.address?.city ?: "Không xác định"
                binding.tvJoinDate.text = "Tham gia từ: ${
                    SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(it.createdAt?.toDate() ?: "")
                }"
                Glide.with(this)
                    .load(it.avatarUrl ?: R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .circleCrop()
                    .into(binding.ivShopAvatar)
            }
        }

        viewModel.products.observe(this) { products ->
            productAdapter.updateProducts(products)
            binding.tvProductCount.text = products.size.toString()
        }

        viewModel.error.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }
}