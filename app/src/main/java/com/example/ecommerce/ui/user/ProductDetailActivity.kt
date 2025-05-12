package com.example.ecommerce.ui.user

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapter.user.ImageSliderAdapter
import com.example.ecommerce.adapter.user.ReviewAdapter
import com.example.ecommerce.model.Product
import com.example.ecommerce.viewmodel.user.ProductDetailViewModel
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.UserActivityProductDetailBinding

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: UserActivityProductDetailBinding
    private lateinit var viewModel: ProductDetailViewModel
    private lateinit var imageSliderAdapter: ImageSliderAdapter
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(ProductDetailViewModel::class.java)

        // Lấy product từ Intent
        val product = intent.getParcelableExtra<Product>("product")
        product?.let { viewModel.loadProduct(it.id) }

        setupToolbar()
        setupAdapters()
        setupObservers()
        setupListeners()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupAdapters() {
        imageSliderAdapter = ImageSliderAdapter()
        binding.imageSlider.adapter = imageSliderAdapter


        reviewAdapter = ReviewAdapter()
        binding.rvReviews.layoutManager = LinearLayoutManager(this)
        binding.rvReviews.adapter = reviewAdapter
    }

    private fun setupObservers() {
        viewModel.product.observe(this) { product ->
            product?.let {
                binding.tvPrice.text = "₫${it.price}"
                binding.tvProductName.text = it.name
                binding.ratingBar.rating = it.rating.toFloat()
                binding.tvRating.text = it.rating.toString()
                binding.tvSoldCount.text = it.soldCount.toString()
                binding.tvDescription.text = it.description
                imageSliderAdapter.updateImages(it.imageUrls)
            }
        }

        viewModel.seller.observe(this) { seller ->
            seller?.let {
                binding.tvShopName.text = it.shopName ?: it.name
                binding.tvShopLocation.text = it.address?.city
                Glide.with(this)
                    .load(it.avatarUrl ?: R.drawable.ic_logo)
                    .placeholder(R.drawable.ic_logo)
                    .into(binding.ivShopAvatar)
            }
        }

        viewModel.reviews.observe(this) { reviews ->
            reviewAdapter.submitList(reviews)
            val avgRating = reviews.map { it.rating }.average().toString().take(3)
            binding.tvAverageRating.text = avgRating
            binding.tvReviewCount.text = "${reviews.size} đánh giá"
        }

        viewModel.error.observe(this) { error ->
            error?.let { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() }
        }
    }

    private fun setupListeners() {
        binding.btnAddToCart.setOnClickListener {
            Toast.makeText(this, "Thêm vào giỏ hàng", Toast.LENGTH_SHORT).show()
        }

        binding.btnBuyNow.setOnClickListener {
            Toast.makeText(this, "Mua ngay", Toast.LENGTH_SHORT).show()
        }

        binding.btnViewShop.setOnClickListener {
            Toast.makeText(this, "Xem Shop", Toast.LENGTH_SHORT).show()
        }

        binding.tvSeeAllReviews.setOnClickListener {
            Toast.makeText(this, "Xem tất cả đánh giá", Toast.LENGTH_SHORT).show()
        }

        binding.btnChat.setOnClickListener {
            Toast.makeText(this, "Chat với Shop", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}